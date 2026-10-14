import assert from 'node:assert/strict';
import { execFileSync } from 'node:child_process';
import { existsSync, mkdirSync, readFileSync } from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import test from 'node:test';
import { fixture } from './fixtures.mjs';
import { rulingConfig } from '../../../ruling-bot.config.mjs';

function helper(f, script, args, env = {}) {
  return execFileSync(process.execPath, [
    fileURLToPath(new URL(`../${script}.mjs`, import.meta.url)), ...args,
  ], {
    cwd: f.workspace,
    encoding: 'utf8',
    env: { ...process.env, RULING_REPOSITORY_PATH: f.workspace, ...env },
  });
}

test('HTML reports use the tested sources submodule, root source paths and HTML snippets', t => {
  const f = fixture(t);
  f.write('its/sources/folder/page with space.html', '<html>\n<img>\n</html>\n');
  // Record an exact sources gitlink without any network/submodule checkout.
  f.git(['update-index', '--add', '--cacheinfo', `160000,${f.head},its/sources`]);
  f.git(['commit', '-m', 'Tested HTML sources']);
  const base = f.git(['rev-parse', 'HEAD']);
  f.write('its/ruling/src/test/resources/expected/project/Web-S1234.json', {
    'project:folder/page with space.html': [2],
  });
  const body = helper(f, 'generate-report', [rulingConfig['old-results-path']], {
    BASE_SHA: base,
    SOURCES_PATH: rulingConfig['sources-path'],
    SOURCES_REPO_URL: rulingConfig['sources-repo-url'],
  });
  assert.match(body, /```html\n[\s\S]*>\s+2 \| <img>/);
  assert.ok(body.includes(`/blob/${f.head}/folder/page%20with%20space.html#L2`));
  assert.doesNotMatch(body, /\/project\/folder|snippet not available/);
  assert.match(body, /S1234\/web/);
});

test('HTML synchronization preserves one project directory and mirrors file deletions', t => {
  const f = fixture(t);
  const actual = rulingConfig['new-results-path'];
  const expected = rulingConfig['old-results-path'];
  f.write(`${actual}/project/Web-S1234.json`, { 'project:page.html': [2] });
  f.write(`${actual}/ruling-results-complete.txt`, '');
  f.write(`${expected}/project/Web-S1234.json`, { 'project:page.html': [1] });
  f.write(`${expected}/project/Web-Stale.json`, { 'project:stale.html': [3] });
  helper(f, 'sync-results', [actual, expected]);
  const destination = path.join(f.workspace, expected);
  assert.deepEqual(JSON.parse(readFileSync(path.join(destination, 'project/Web-S1234.json'))), {
    'project:page.html': [2],
  });
  assert.equal(existsSync(path.join(destination, 'project/Web-Stale.json')), false);
  assert.equal(existsSync(path.join(destination, 'project/project')), false);
  assert.equal(existsSync(path.join(destination, 'ruling-results-complete.txt')), false);
});

test('HTML reports can disable snippets and retain root source links for legacy rule names', t => {
  const f = fixture(t);
  f.write('display-results/project/Web-ImgWithoutAltCheck.json', { 'project:page.html': [1] });
  const body = helper(f, 'generate-report', ['display-results'], {
    BASE_SHA: f.merge,
    SOURCES_PATH: '',
    SOURCES_REPO_URL: rulingConfig['sources-repo-url'],
  });
  assert.ok(body.includes('/blob/master/page.html#L1'));
  assert.doesNotMatch(body, /\/project\/page.html|\/ImgWithoutAltCheck\/web|```/);
});

test('the local report command forwards repository configuration to its renderer subprocess', t => {
  const f = fixture(t);
  const expected = 'custom-expectations';
  f.write('.github/ruling-bot.config.mjs', `export default ${JSON.stringify({
    ...rulingConfig,
    'old-results-path': expected,
    'sources-path': '',
    'sources-repo-url': 'https://example.test/html/blob/main',
    'rspec-base-url': '',
  })};`);
  f.write(`${expected}/project/Web-S1234.json`, { 'project:page.html': [2] });
  const body = helper(f, 'local-report', [], { BASE_SHA: f.merge });
  assert.match(body, /page\.html:2/);
  assert.ok(body.includes('https://example.test/html/blob/main/page.html#L2'));
  assert.doesNotMatch(body, /sonarsource.github.io\/rspec|```/);
});

test('a complete HTML snapshot with zero issues deletes all obsolete expectations', t => {
  const f = fixture(t);
  const actual = rulingConfig['new-results-path'];
  const expected = rulingConfig['old-results-path'];
  mkdirSync(path.join(f.workspace, actual, 'project'), { recursive: true });
  f.write(`${actual}/ruling-results-complete.txt`, '');
  f.write(`${expected}/project/Web-S1234.json`, { 'project:page.html': [1] });
  helper(f, 'sync-results', [actual, expected]);
  assert.equal(existsSync(path.join(f.workspace, expected, 'project/Web-S1234.json')), false);
});

for (const invalidEntry of [
  'Web-S1234.json',
  'project/project/Web-S1234.json',
  'project/Web-S1234.sarif',
]) {
  test(`HTML synchronization rejects ${invalidEntry} before deleting expectations`, t => {
    const f = fixture(t);
    const actual = rulingConfig['new-results-path'];
    const expected = rulingConfig['old-results-path'];
    mkdirSync(path.join(f.workspace, actual, 'project'), { recursive: true });
    f.write(`${actual}/ruling-results-complete.txt`, '');
    f.write(`${actual}/${invalidEntry}`, {});
    const baseline = `${expected}/project/Web-S1234.json`;
    f.write(baseline, { 'project:page.html': [1] });
    assert.throws(() => helper(f, 'sync-results', [actual, expected]), /Unexpected result entry|Unsupported result entry/);
    assert.deepEqual(JSON.parse(readFileSync(path.join(f.workspace, baseline))), {
      'project:page.html': [1],
    });
  });
}

test('HTML reports reject SARIF expectations instead of silently omitting their changes', t => {
  const f = fixture(t);
  f.write('display-results/project/Web-S1234.sarif', { runs: [] });
  assert.throws(() => helper(f, 'generate-report', ['display-results'], {
    BASE_SHA: f.merge,
  }), /SARIF ruling reports are not supported/);
});
