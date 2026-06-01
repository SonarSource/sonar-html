<?php
echo "<a href='x'></a>";         // Noncompliant: empty anchor in PHP string literal
echo "<a href='y'>Click me</a>"; // Compliant: has text content
echo "<a></a>";                   // Noncompliant: empty anchor
?>
