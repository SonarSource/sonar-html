<?php
$out  = "<div aria-hidden=\"not-a-bool\"></div>"; // Noncompliant: not-a-bool is not a valid boolean
$out .= "<div aria-hidden=\"true\"></div>";         // Compliant
$out .= "<div aria-hidden=\"$flag\"></div>";        // Compliant: dynamic value
?>
