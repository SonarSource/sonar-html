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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
  <s:i18n name="localization.Continuum">
    <head>
        <title><s:text name="releasePerformFromScm.page.title"/></title>
    </head>
    <body>
      <h2><s:text name="releasePerformFromScm.section.title"/></h2>
      <s:form action="releasePerformFromScm" method="post" validate="true">
        <h3><s:text name="releasePerformFromScm.parameters.section.title"/></h3>
        <s:hidden name="projectId"/>
        <div class="axial">
          <table border="1" cellspacing="2" cellpadding="3" width="100%">
            <s:textfield label="%{getText('releasePerformFromScm.scmUrl.label')}" name="scmUrl"/>
            <s:textfield label="%{getText('releasePerformFromScm.scmUsername.label')}" name="scmUsername"/>
            <s:password label="%{getText('releasePerformFromScm.scmPassword.label')}" name="scmPassword"/>
            <s:textfield label="%{getText('releasePerformFromScm.scmTag.label')}" name="scmTag"/>
            <c:if test="${!empty (scmTagBase)}">
              <s:textfield label="%{getText('releasePerformFromScm.scmTagBase.label')}" name="scmTagBase"/>
            </c:if>
            <s:textfield label="%{getText('releasePerformFromScm.goals.label')}" name="goals"/>
            <s:textfield label="%{getText('releasePrepare.arguments.label')}" name="arguments"/>
            <s:checkbox label="%{getText('releasePerformFromScm.useReleaseProfile.label')}" name="useReleaseProfile"/>
            <s:select label="%{getText('releasePerformFromScm.buildEnvironment.label')}" name="profileId" list="profiles" listValue="name"
                       listKey="id" headerKey="-1" headerValue=""/>
          </table>
        </div>
        <s:submit value="%{getText('submit')}"/>
      </s:form>
    </body>
  </s:i18n>
</html>
