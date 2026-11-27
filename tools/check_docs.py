"""AP-1529 stopgap checks until the shared documentation action is published."""

from collections import defaultdict
from dataclasses import dataclass, field
from datetime import date
from html.parser import HTMLParser
import os
from pathlib import Path
import re
import shlex
import subprocess
import sys
import tomllib
from urllib.parse import unquote, urlsplit

from markdown_it import MarkdownIt
from markdown_it.token import Token
import yaml


AGENT_FILE = "AGENTS.md"
INDEX_FILE = "index.md"
SKILL_FILE = "SKILL.md"
QA_ENV = "SONARSOURCE_QA=true"
ROOT_AGENTS = Path(AGENT_FILE)
DOCS = Path("docs")
CONVENTIONS = DOCS / "documentation-conventions.md"
DOC_INDEX = DOCS / INDEX_FILE
SKILLS = Path(".agents/skills")
ADAPTERS = (Path(".claude/skills"), Path(".cursor/skills"))
REGENERATION = SKILLS / "regenerate-rule-metadata" / SKILL_FILE
BUILD = Path(".github/workflows/build.yml")
MISE = Path("mise.toml")
TEST_ROOT = Path("sonar-html-plugin/src/test/java")
TEST_CLASS = "LabelHasAssociatedControlCheckTest"
REQUIRED_SECTIONS = ("Scope", "Commands", "Constraints", "Documentation")
LOCAL_COMMANDS = (
    "mise install",
    "mvn clean install",
    f"mvn test -Dtest={TEST_CLASS} -pl sonar-html-plugin",
    f"mvn test -Dtest={TEST_CLASS}#razor -pl sonar-html-plugin",
    "git submodule update --init",
)
VORTEX_BLOCK = re.compile(
    r"^<!-- sonar:begin:sonarqube-agentic-analysis-protocol -->\n.*?"
    r"^<!-- sonar:end:sonarqube-agentic-analysis-protocol -->$", re.MULTILINE | re.DOTALL
)
FRONTMATTER = re.compile(r"\A---[ \t]*\n(.*?)^---[ \t]*(?:\n|$)", re.MULTILINE | re.DOTALL)
PATH_SYNTAX = re.compile(r"[.\w-]+(?:/[.\w-]+)*/?", re.ASCII)
FILE_SUFFIXES = {
    ".css", ".gif", ".html", ".java", ".jpeg", ".jpg", ".js", ".json", ".md", ".markdown",
    ".png", ".properties", ".py", ".sarif", ".sh", ".svg", ".toml", ".ts", ".txt", ".xml",
    ".yaml", ".yml",
}
MARKDOWN = MarkdownIt("commonmark").enable("table")
MD_SUFFIXES = {".md", ".markdown"}


@dataclass
class Report:
    errors: list[str] = field(default_factory=list)
    warnings: list[str] = field(default_factory=list)

    def error(self, path, message):
        self.errors.append(f"{path}: {message}")

    def warn(self, path, message):
        self.warnings.append(f"{path}: {message}")


@dataclass
class Document:
    raw: bytes
    text: str
    metadata: str | None
    tokens: list[Token]
    line_offset: int

    @classmethod
    def read(cls, path):
        raw = path.read_bytes()
        text = raw.decode("utf-8").replace("\r\n", "\n")
        match = FRONTMATTER.match(text)
        body = text[match.end():] if match else text
        # Parse hand-written guidance, not tool-managed sample paths/commands. Size and
        # line limits still apply to the complete file, including this block.
        if path.name == AGENT_FILE:
            body = VORTEX_BLOCK.sub(lambda m: "\n" * m[0].count("\n"), body)
        return cls(raw, text, match[1] if match else None, MARKDOWN.parse(body),
                   text[:match.end()].count("\n") if match else 0)

    def line(self, token):
        return self.line_offset + (token.map[0] if token.map else 0) + 1

    def inline(self):
        for token in self.tokens:
            if token.type == "inline":
                for child in token.children:
                    yield self.line(token), child


def date_value(value):
    if type(value) is date:
        return value
    if isinstance(value, str) and re.fullmatch(r"\d{4}-\d{2}-\d{2}", value):
        try:
            return date.fromisoformat(value)
        except ValueError:
            pass
    return None


def nonempty(value):
    return isinstance(value, str) and bool(value.strip())


def check_frontmatter(path, document, report, today):
    try:
        data = yaml.safe_load(document.metadata) if document.metadata is not None else None
    except yaml.YAMLError as exc:
        report.error(path, f"invalid YAML frontmatter: {exc}")
        return
    if not isinstance(data, dict):
        report.error(path, "expected a YAML frontmatter mapping")
        return
    for key in ("title", "description"):
        if not nonempty(data.get(key)):
            report.error(path, f"frontmatter requires a non-empty {key} string")
    if path == DOC_INDEX and data.get("okf_version") != "0.2":
        report.error(path, 'frontmatter requires okf_version: "0.2" (string)')
    check_provenance(path, data, report)
    if "stale_after" in data:
        check_expiry(path, data["stale_after"], today, report)
    if path == CONVENTIONS:
        check_baseline(path, data.get("baseline"), report)


def check_provenance(path, data, report):
    if "generated" in data:
        generated = data["generated"]
        if not isinstance(generated, dict) or not nonempty(generated.get("by")):
            report.error(path, "generated.by must be a single non-empty string")
    if "sources" in data:
        sources = data["sources"]
        if not isinstance(sources, list) or not sources or any(
            not isinstance(source, dict) or not nonempty(source.get("resource")) for source in sources
        ):
            report.error(path, "sources must be a non-empty list of entries with a resource string (omit if unsourced)")


def check_expiry(path, value, today, report):
    expiry = date_value(value)
    if expiry is None:
        report.error(path, "stale_after must be a YYYY-MM-DD date")
    elif expiry < today:
        report.warn(path, f"stale_after {expiry} has passed; review this document")


def check_baseline(path, baseline, report):
    if not isinstance(baseline, dict):
        report.error(path, "frontmatter requires baseline.resource, baseline.checked and baseline.template")
        return
    resource = baseline.get("resource")
    if not nonempty(resource) or not re.fullmatch(r"https?://[^\s/]+/[^\s]+", resource):
        report.error(path, "baseline.resource must link to the Repository Baseline")
    if date_value(baseline.get("checked")) is None:
        report.error(path, "baseline.checked must be a YYYY-MM-DD date")
    if not nonempty(baseline.get("template")):
        report.error(path, "baseline.template must record the template version or that it is not yet published")


def command_lines(script):
    for line in script.replace("\\\n", "").splitlines():
        # Strip shell comments without stripping #razor from the Maven argument.
        line = re.split(r"(?:^|\s)#", line, maxsplit=1)[0].strip()
        if line:
            yield tuple(shlex.split(line))


def workflow_commands(root, report):
    expected = set()
    try:
        jobs = yaml.safe_load((root / BUILD).read_text(encoding="utf-8"))["jobs"]
        actions = {step.get("uses", "").partition("@")[0] for step in jobs["build"]["steps"]}
        if not {"jdx/mise-action", "SonarSource/ci-github-actions/build-maven"} <= actions:
            report.error(BUILD, "build job no longer uses mise and build-maven; review AGENTS.md Commands")
        for job in ("qa", "ruling"):
            maven = [
                argv for step in jobs[job]["steps"]
                if str(step.get("env", {}).get("SONARSOURCE_QA")).lower() == "true"
                for argv in command_lines(step.get("run", "")) if argv[0] == "mvn"
            ]
            if len(maven) != 1:
                report.error(BUILD, f"expected one Maven command with SONARSOURCE_QA=true in the {job} job")
            else:
                expected.add(maven[0])
    except (OSError, KeyError, TypeError, ValueError, AttributeError, yaml.YAMLError) as exc:
        report.error(BUILD, f"cannot read build, qa and ruling jobs: {exc}")
    return expected


def check_unit_test_target(root, paths, report):
    tests = [path for path in paths if path.is_relative_to(TEST_ROOT) and path.name == f"{TEST_CLASS}.java"]
    if len(tests) != 1:
        report.error(ROOT_AGENTS, f"Commands must name an existing, unambiguous {TEST_CLASS} test")
    elif not re.search(r"\bvoid\s+razor\s*\(", (root / tests[0]).read_text(encoding="utf-8")):
        report.error(tests[0], f"the test method in AGENTS.md Commands ({TEST_CLASS}#razor) no longer exists")


def check_mise_toolchain(root, report):
    try:
        tools = tomllib.loads((root / MISE).read_text(encoding="utf-8")).get("tools", {})
        for tool in ("java", "maven"):
            version = tools.get(tool)
            if isinstance(version, dict):
                version = version.get("version")
            if not nonempty(version):
                report.error(MISE, f"[tools] must declare a {tool} version for AGENTS.md Commands")
    except (OSError, ValueError, AttributeError) as exc:
        report.error(MISE, f"cannot read the mise toolchain: {exc}")



def check_commands(root, body, report):
    check_mise_toolchain(root, report)
    expected = {tuple(command.split()) for command in LOCAL_COMMANDS} | workflow_commands(root, report)
    actual = set()
    for token in body:
        is_shell = token.type == "fence" and token.info.strip().partition(" ")[0] in {"", "bash", "sh", "shell", "zsh"}
        if token.type != "code_block" and not is_shell:
            continue
        try:
            actual.update(command_lines(token.content))
        except ValueError as exc:
            report.error(ROOT_AGENTS, f"invalid shell command in Commands: {exc}")
    if ("export", QA_ENV) not in actual and not any(
        token.type == "inline" and QA_ENV in token.content for token in body
    ):
        report.error(ROOT_AGENTS, f"Commands must document {QA_ENV} for QA and ruling")
    actual.discard(("export", QA_ENV))
    for command in sorted(expected - actual):
        report.error(ROOT_AGENTS, f"Commands is missing `{' '.join(command)}` (match build.yml for QA and ruling)")
    for command in sorted(actual - expected):
        report.error(ROOT_AGENTS, f"Commands has an unexpected command: `{' '.join(command)}`")


def check_root_sections(root, document, report):
    headings = [(i, token.tag, document.tokens[i + 1].content) for i, token in enumerate(document.tokens)
                if token.type == "heading_open" and token.level == 0]
    if headings and headings[0][1:] in {("h1", AGENT_FILE), ("h1", "AGENTS")}:
        headings.pop(0)
    headings = [(i, title) for i, _, title in headings]
    if tuple(title for _, title in headings[:4]) != REQUIRED_SECTIONS:
        report.error(ROOT_AGENTS, "start with Scope, Commands, Constraints and Documentation, in that order")
        return
    for position, (i, title) in enumerate(headings[:4]):
        end = headings[position + 1][0] if position + 1 < len(headings) else len(document.tokens)
        body = document.tokens[i + 3:end]
        if not any(token.type in {"inline", "fence", "code_block"} and token.content.strip() for token in body):
            report.error(ROOT_AGENTS, f"{title} section is empty")
        if title == "Commands":
            check_commands(root, body, report)


def check_agent_files(root, paths, documents, report):
    if ROOT_AGENTS in documents:
        check_root_sections(root, documents[ROOT_AGENTS], report)
        check_unit_test_target(root, paths, report)
    for path in sorted(paths):
        if path.name == "CLAUDE.md" and path.with_name(AGENT_FILE) not in documents:
            report.error(path, "unexpected CLAUDE.md without a sibling AGENTS.md")
    for path, document in documents.items():
        if path.name == AGENT_FILE:
            check_agent_file(root, path, document, paths, report)


def check_agent_file(root, path, document, paths, report):
    size_limit = 8192 if path == ROOT_AGENTS else 4096
    if len(document.raw) > size_limit:
        report.error(path, f"exceeds the {size_limit // 1024} KiB limit (including managed blocks)")
    if len(document.text.splitlines()) > 150:
        report.error(path, "exceeds the 150-line limit (including managed blocks)")
    importer = path.with_name("CLAUDE.md")
    if importer not in paths or (root / importer).is_symlink() or (
        (root / importer).read_bytes() not in {b"@AGENTS.md\n", b"@AGENTS.md\r\n", b"@AGENTS.md"}
    ):
        report.error(importer, "must contain only @AGENTS.md")


class Anchors(HTMLParser):
    def __init__(self):
        super().__init__()
        self.ids = set()

    def handle_starttag(self, tag, attrs):
        self.ids.update(value for key, value in attrs if value and (key == "id" or (tag == "a" and key == "name")))


def anchors(document):
    html = Anchors()
    slugs = set()
    for i, token in enumerate(document.tokens):
        if token.type == "heading_open":
            text = "".join(child.content for child in document.tokens[i + 1].children
                           if child.type in {"text", "code_inline", "image"})
            base = re.sub(r"[^\w\- ]", "", text.strip().lower()).replace(" ", "-")
            slug, counter = base, 0
            while slug in slugs:
                counter += 1
                slug = f"{base}-{counter}"
            slugs.add(slug)
        elif token.type == "html_block":
            html.feed(token.content)
    for _, token in document.inline():
        if token.type == "html_inline":
            html.feed(token.content)
    return html.ids | slugs


def repo_reference(text, paths, dirs):
    if not PATH_SYNTAX.fullmatch(text):
        return None
    path = Path(text)
    if not path.parts or ".." in path.parts:
        return None
    # Bare words and owner/repo names are ambiguous; a file suffix, leading dot,
    # trailing slash or a known top-level directory makes the intent clear.
    if (path in paths or text.endswith("/") or path.name in {".gitignore", ".gitmodules"} or
            path.suffix.lower() in FILE_SUFFIXES or
            (len(path.parts) > 1 and (text.startswith(".") or Path(path.parts[0]) in dirs))):
        return path
    return None


def directory_index(path, documents):
    for name in (INDEX_FILE, "README.md"):
        if path / name in documents:
            return path / name
    return path


def markdown_target(source, link):
    url = urlsplit(link)
    if url.scheme or url.netloc or link.startswith("//"):
        return None, None
    name = unquote(url.path)
    if not name:
        return source, unquote(url.fragment)
    # GitHub supports a leading slash for root-relative links. Otherwise a
    # Markdown link is relative to its source file, never to the repository root.
    path = source.parent / name if not name.startswith("/") else Path(name.lstrip("/"))
    return Path(os.path.normpath(path)), unquote(url.fragment)


@dataclass
class ReferenceChecker:
    paths: set
    directories: set
    documents: dict
    report: Report
    document_anchors: dict = field(init=False)

    def __post_init__(self):
        self.document_anchors = {path: anchors(doc) for path, doc in self.documents.items()}

    def check_link(self, source, location, link):
        try:
            target, fragment = markdown_target(source, link)
        except ValueError:
            self.report.error(location, f"invalid Markdown link `{link}`")
            return None
        if target is None:
            return None
        if target not in self.paths and target not in self.directories:
            self.report.error(location, f"Markdown link `{link}` does not resolve relative to {source.parent}")
            return None
        target = self.link_target(target)
        if fragment and target in self.document_anchors and fragment not in self.document_anchors[target]:
            self.report.error(location, f"Markdown link `{link}` has no matching anchor in {target}")
        return target

    def check_backticked_path(self, location, text):
        target = repo_reference(text, self.paths, self.directories)
        if target is None:
            return None
        if target not in self.paths and target not in self.directories:
            self.report.error(location, f"repo-relative path `{text}` does not exist")
            return None
        return self.link_target(target)

    def link_target(self, target):
        return directory_index(target, self.documents) if target in self.directories else target

    def targets(self, source, document):
        in_link = False
        for line, token in document.inline():
            location = f"{source}:{line}"
            if token.type == "link_close":
                in_link = False
            elif token.type == "link_open":
                in_link = True
                yield self.check_link(source, location, token.attrGet("href"))
            elif token.type == "image":
                self.check_link(source, location, token.attrGet("src"))
            elif token.type == "code_inline" and not in_link:
                yield self.check_backticked_path(location, token.content)

    def edges(self):
        return defaultdict(set, {
            source: {target for target in self.targets(source, doc) if target is not None}
            for source, doc in self.documents.items()
        })


def check_adapters(paths, documents, report):
    fixed_folder = REGENERATION.parent.name
    for prefix in ADAPTERS:
        folders = {path.parts[len(prefix.parts)] for path in paths
                   if path.is_relative_to(prefix) and len(path.parts) > len(prefix.parts)}
        for folder in sorted(folders | {fixed_folder}):
            check_adapter(prefix / folder / SKILL_FILE, SKILLS / folder / SKILL_FILE, documents, report)


def check_adapter(path, canonical, documents, report):
    if path not in documents:
        report.error(path, f"missing Markdown adapter for {canonical}")
        return
    if canonical not in documents:
        report.error(path, f"matching canonical skill {canonical} does not exist")
    document = documents[path]
    links = [token.attrGet("href") for _, token in document.inline() if token.type == "link_open"]
    structure = [token.type for token in document.tokens if token.level == 0]
    try:
        target = markdown_target(path, links[0]) if len(links) == 1 else None
    except ValueError:
        target = None
    paragraph = ["paragraph_open", "paragraph_close"]
    if structure not in (paragraph, ["heading_open", "heading_close", *paragraph]) or target != (canonical, ""):
        report.error(path, f"must be a Markdown stub containing one link to {canonical}")


def check_reachability(edges, required, report):
    pending = [ROOT_AGENTS]
    seen = set()
    while pending:
        current = pending.pop()
        if current not in seen:
            seen.add(current)
            pending.extend(edges[current] - seen)
    for path in sorted(required - seen):
        report.error(path, "not reachable from AGENTS.md through Markdown links or backticked paths")


def load_documents(root, paths, report):
    documents = {}
    for path in sorted(paths):
        if path.suffix.lower() not in MD_SUFFIXES:
            continue
        try:
            if (root / path).is_symlink():
                report.error(path, "expected a Markdown file, not a symlink")
                continue
            documents[path] = Document.read(root / path)
        except (OSError, UnicodeError) as exc:
            report.error(path, f"cannot read Markdown: {exc}")
    return documents


def run_checks(root, paths, today=None):
    report = Report()
    paths = {path for path in paths if (root / path).exists()}
    directories = {parent for path in paths for parent in path.parents}
    directories.update(path for path in paths if (root / path).is_dir() and not (root / path).is_symlink())
    documents = load_documents(root, paths, report)
    required = {ROOT_AGENTS, DOC_INDEX, CONVENTIONS, REGENERATION}
    required.update(path / INDEX_FILE for path in directories if path.is_relative_to(DOCS))
    for path in sorted(required - documents.keys()):
        report.error(path, "required Markdown file is missing")
    check_agent_files(root, paths, documents, report)
    check_adapters(paths, documents, report)
    edges = ReferenceChecker(paths, directories, documents, report).edges()

    reachable = {path for path in documents if path.name == AGENT_FILE or path.is_relative_to(DOCS)}
    reachable.update(path for path in documents if path.is_relative_to(SKILLS) and path.name == SKILL_FILE)
    check_reachability(edges, reachable, report)
    if REGENERATION in documents and REGENERATION not in edges[ROOT_AGENTS]:
        report.error(ROOT_AGENTS, f"must link directly to the canonical skill {REGENERATION}")
    for path, document in documents.items():
        if path.is_relative_to(DOCS):
            check_frontmatter(path, document, report, today or date.today())
        if path.name != AGENT_FILE and len(document.text.splitlines()) > 300:
            report.error(path, "exceeds the 300-line limit")
    return report


def git_paths(root):
    # Include non-ignored new files for local use without staging anything. The
    # Git inventory also avoids walking into ruling sources or generated builds.
    output = subprocess.check_output(
        ["git", "ls-files", "--cached", "--others", "--exclude-standard", "-z"], cwd=root
    )
    return {Path(os.fsdecode(name)) for name in output.split(b"\0") if name}


def main():
    root = Path(__file__).resolve().parent.parent
    try:
        report = run_checks(root, git_paths(root))
    except (OSError, subprocess.CalledProcessError) as exc:
        print(f"error: cannot check repository documentation: {exc}", file=sys.stderr)
        return 1
    for label, messages in (("warning", report.warnings), ("error", report.errors)):
        for message in messages:
            print(f"{label}: {message}", file=sys.stderr)
    if not report.errors:
        print("Repository documentation checks passed.")
    return int(bool(report.errors))


if __name__ == "__main__":
    sys.exit(main())
