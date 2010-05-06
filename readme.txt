Sonar Web Plugin
Status: Prototype 
Author: Matthijs Galesloot 

The sonar web plugin has been tested on a sonar 2.0 server. 

The plugin performs source importing, line counting and checks on web files. 
Currently the following file extensions are supported: "html", "xhtml", "js", "css", "jspf", "jsp". 
 
1. Installation 
- Copy sonar-channel-2.2-SNAPSHOT.jar to the folder extensions\plugins
- Copy sonar-web-plugin-0.1-SNAPSHOT.jar to the folder extensions\plugins
- Restart sonar 

2. Running analysis on a web project
Create a pom file for the web project. 
Make sure to set sonar.language to web. 
Set the source directory to the location of the webfiles. 

Example: 

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>test</groupId>
  <artifactId>test</artifactId>
  <version>1.0</version>
  <packaging>pom</packaging>
  <name>test</name>
  <build>
    <sourceDirectory>src/main/webapp</sourceDirectory>
  </build> 
  <properties>
    <sonar.language>web</sonar.language>
    <sonar.dynamicAnalysis>false</sonar.dynamicAnalysis>
  </properties>
</project>

