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
      <title><s:text name="releaseProject.page.title"/></title>
    </head>
    <body>
      <h2><s:text name="releasePrepare.section.title"/></h2>
      <s:form action="releasePrepare" method="post">
        <h3><s:text name="releasePrepare.parameters"/></h3>
        <input type="hidden" name="projectId" value="<s:property value="projectId"/>"/>
        <input type="hidden" name="autoVersionSubmodules" value="<s:property value="autoVersionSubmodules"/>"/>
        <div class="axial">
          <table border="1" cellspacing="2" cellpadding="3" width="100%">
            <s:textfield label="%{getText('releasePrepare.scmUsername.label')}" name="scmUsername"/>
            <s:password label="%{getText('releasePrepare.scmPassword.label')}" name="scmPassword"/>
            <s:textfield label="%{getText('releasePrepare.scmTag.label')}" name="scmTag" required="true"/>
            <c:if test="${!empty (scmTagBase)}">
              <s:textfield label="%{getText('releasePrepare.scmTagBase.label')}" name="scmTagBase"/>
            </c:if>
            <s:textfield label="%{getText('releasePrepare.scmCommentPrefix.label')}" name="scmCommentPrefix"/>
            <s:textfield label="%{getText('releasePrepare.prepareGoals.label')}" name="prepareGoals" required="true"/>
            <s:textfield label="%{getText('releasePrepare.arguments.label')}" name="arguments"/>
            <s:select label="%{getText('releasePrepare.buildEnvironment.label')}" name="profileId" list="profiles" listValue="name"
                       listKey="id" headerKey="-1" headerValue=""/>
			      <s:checkbox label="%{getText('releasePrepare.useEditMode.label')}" name="scmUseEditMode" fieldValue="false"/>
            <s:checkbox label="%{getText('releasePrepare.addSchema.label')}" name="addSchema" fieldValue="false"/>
            <c:if test="${autoVersionSubmodules}">
              <s:checkbox label="%{getText('releasePrepare.autoVersionSubmodules.label')}" name="autoVersionSubmodules" disabled="true" fieldValue="false"/>
            </c:if>
          </table>
        </div>

        <s:iterator value="projects">
          <h3><s:property value="name"/></h3>
          <input type="hidden" name="projectKeys" value="<s:property value="key"/>">
          <div class="axial">
            <table border="1" cellspacing="2" cellpadding="3" width="100%">
              <tr>
                <th><s:text name="releasePrepare.releaseVersion"/></th>
                <td>
                  <input type=text name="relVersions"
                         value="<s:property value="release"/>" size="100">
                </td>
              </tr>
              <tr>
                <th><s:text name="releasePrepare.nextDevelopmentVersion"/></th>
                <td>
                  <input type=text name="devVersions"
                         value="<s:property value="dev"/>" size="100">
                </td>
              </tr>
             </table>
           </div>
        </s:iterator>

        <s:submit value="%{getText('submit')}"/>
      </s:form>
    </body>
  </s:i18n>
</html>
