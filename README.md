Code Quality and Security for HTML
====================

[![Build Status](https://api.travis-ci.org/SonarSource/sonar-html.svg)](https://travis-ci.org/SonarSource/sonar-html)

Useful links
------------

* [Project homepage](https://redirect.sonarsource.com/plugins/web.html)
* [Issue tracking](https://jira.sonarsource.com/browse/SONARHTML/)
* [Available rules](https://rules.sonarsource.com/html)
* [SonarQube Community Forum](https://community.sonarsource.com/)

Have question or feedback?
--------------------------

To provide feedback (request a feature, report a bug etc.) use the [SonarQube Community Forum](https://community.sonarsource.com/). Please do not forget to specify the language (HTML!), plugin version and SonarQube version.

If you have a question on how to use plugin (and the [docs](https://docs.sonarqube.org/latest/analysis/languages/html/) don't help you), we also encourage you to use the community forum.


### Build the Project and Run Unit Tests

To build the plugin and run its unit tests, execute this command from the project's root directory:

    mvn clean install
or

    mvn clean verify

### Integration Tests

By default, Integration Tests (ITs) are skipped during build. If you want to run them, you need first to retrieve the related projects which are used as input:

    git submodule init 
    git submodule update

Integration tests consist of Plugin tests and Ruling tests. To run them both you need to activate its profile (Make sure you've built the project and plugin .jar is up-to-date before running its, otherwise you might receive outdated results):

    mvn verify -Pits

#### Plugin Test

The "Plugin Test" is an integration test suite which verifies plugin features such as metric calculation etc. To launch it:

    cd its/plugin 
    mvn verify

#### Ruling Test

The "Ruling Test" are an integration test suite which launches the analysis of a large code base, saves the issues created by the plugin in report files, and then compares those results to the set of expected issues (stored as JSON files). Launch ruling test:

    cd its/ruling
    mvn verify

### License

Copyright 2010-2022 SonarSource.

Licensed under the [GNU Lesser General Public License, Version 3.0](http://www.gnu.org/licenses/lgpl.txt)
