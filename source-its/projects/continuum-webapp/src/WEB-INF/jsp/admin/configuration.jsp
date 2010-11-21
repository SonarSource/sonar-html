<%--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements.  See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership.  The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License.  You may obtain a copy of the License at
  ~
  ~   http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.  See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  --%>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="continuum" prefix="c1" %>
<html>
<s:i18n name="localization.Continuum">
  <head>
    <title>
      <s:text name="configuration.page.title"/>
    </title>
  </head>
  <body>
  <div id="axial" class="h3">
    <h3>
      <s:text name="configuration.section.title"/>
    </h3>

    <div class="axial">
      <table border="1" cellspacing="2" cellpadding="3" width="100%">
        <c1:data label="%{getText('configuration.workingDirectory.label')}" name="workingDirectory"/>
        <c1:data label="%{getText('configuration.buildOutputDirectory.label')}" name="buildOutputDirectory"/>
        <c1:data label="%{getText('configuration.releaseOutputDirectory.label')}" name="releaseOutputDirectory"/>
        <c1:data label="%{getText('configuration.deploymentRepositoryDirectory.label')}"
                 name="deploymentRepositoryDirectory"/>
        <c1:data label="%{getText('configuration.baseUrl.label')}" name="baseUrl"/>
        <c1:data label="%{getText('configuration.allowed.build.parallel')}" name="numberOfAllowedBuildsinParallel"/>
        <c1:data label="%{getText('configuration.distributedBuildEnabled.label')}" name="distributedBuildEnabled"/>
      </table>
      <div class="functnbar3">
        <s:form action="configuration!input.action" method="post">
          <s:submit value="%{getText('edit')}"/>
        </s:form>
      </div>
    </div>
  </div>
  </body>
</s:i18n>
</html>
