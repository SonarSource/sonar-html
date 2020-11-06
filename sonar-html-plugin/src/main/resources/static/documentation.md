---
title: HTML
key: html
---

<!-- static -->
<!-- update_center:web -->
<!-- /static -->

## Language-Specific Properties

You can discover and update HTML-specific [properties](/analysis/analysis-parameters/) in:  <!-- sonarcloud -->Project <!-- /sonarcloud -->**[Administration > General Settings > HTML](/#sonarqube-admin#/admin/settings?category=html)**.

## PHP Code Analysis
PHP and HTML analyzers both analyze files with extensions: `.php`, `.php3`, `.php4`, `.php5`, `.phtml`.

File metrics, such as the number of lines of code, can only be measured by one of the languages, PHP or HTML. They are handled by the PHP analyzer by default, and by HTML analyzer if for some reason the former is not present.

The HTML analyzer inspects PHP files even if the PHP file extensions are not included in the list of file extensions to analyze.
