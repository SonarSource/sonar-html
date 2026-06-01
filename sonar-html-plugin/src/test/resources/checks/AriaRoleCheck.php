<?php
$out  = "<div role=\"foobar\"></div>";           // Noncompliant: foobar is an unknown role
$out .= "<li id=\"fn:$note_id\" role=\"doc-endnote\">";  // Compliant: role is valid
$out .= "<div role=\"$role\"></div>";              // Compliant: role is dynamic
$out .= "<div role=\"toolbar\"></div>";            // Compliant: role is valid
?>
