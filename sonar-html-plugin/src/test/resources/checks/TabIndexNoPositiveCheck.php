<?php
$out  = "<div tabindex=\"5\"></div>";   // Noncompliant: positive tabindex
$out .= "<div tabindex=\"0\"></div>";   // Compliant
$out .= "<div tabindex=\"$i\"></div>";  // Compliant: dynamic value
?>
