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
<html>
  <s:i18n name="localization.Continuum">
    <head>
        <title><s:text name="releaseProject.page.title"/></title>
    </head>
    <body>
      <h3>
        <s:text name="releaseProject.section.title">
          <s:param><s:property value="projectName"/></s:param>
        </s:text>
      </h3>
      <s:form action="releaseProject" method="post">
        <p>
          <input name="goal" type="radio" value="prepare" checked/><s:text name="releaseProject.prepareReleaseOption"/>
          <br/>
          <input name="goal" type="radio" value="perform"/><s:text name="releaseProject.performReleaseOption"/>
          <br/>
          &nbsp;&nbsp;&nbsp;
          <select name="preparedReleaseId">
            <s:if test="preparedReleaseName != null">
              <option selected value="<s:property value="preparedReleaseId"/>">
                <s:property value="preparedReleaseName"/>
              </option>
            </s:if>
            <option value=""><s:text name="releaseProject.provideReleaseParameters"/></option>
          </select>
          <br/>
        </p>
        <input name="projectId" type="hidden" value="<s:property value="projectId"/>"/>
        <s:submit value="%{getText('submit')}"/>
      </s:form>
    </body>
  </s:i18n>
</html>
