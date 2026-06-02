<?php
echo "<a href='x'></a>";         // Noncompliant: empty anchor in PHP string literal
echo "<a href='y'>Click me</a>"; // Compliant: has text content
echo "<a></a>";                   // Noncompliant: empty anchor
echo "<a>" . "</a>";              // Noncompliant: still empty across pure concatenation
echo "<a>" . "Click" . "</a>";   // Compliant: literal text preserved across concatenation
echo "<a>" . $label . "</a>";    // Compliant: runtime expression is opaque non-blank content
?>
