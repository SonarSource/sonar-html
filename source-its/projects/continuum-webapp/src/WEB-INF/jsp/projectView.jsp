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
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="continuum" prefix="c1" %>
<%@ taglib uri="http://plexus.codehaus.org/redback/taglib-1.0" prefix="redback" %>

<html>
  <s:i18n name="localization.Continuum">
    <head>
        <title><s:text name="projectView.page.title"/></title>
    </head>
    <body>
      <div id="h3">

        <jsp:include page="/WEB-INF/jsp/navigations/ProjectMenu.jsp">
          <jsp:param name="tab" value="view"/>
        </jsp:include>

        <h3><s:text name="projectView.section.title"><s:param>${project.name}</s:param></s:text></h3>

        <div class="axial">
          <table border="1" cellspacing="2" cellpadding="3" width="100%">
            <c1:data label="%{getText('projectView.project.name')}" name="project.name"/>
            <c1:data label="%{getText('projectView.project.description')}" name="project.description"/>
            <c1:data label="%{getText('projectView.project.version')}" name="project.version"/>
            <c1:data label="%{getText('projectView.project.scmUrl')}" name="project.scmUrl"/>
            <c1:data label="%{getText('projectView.project.scmTag')}" name="project.scmTag"/>
            <s:url id="projectGroupSummaryUrl" value="/projectGroupSummary.action">
                <s:param name="projectGroupId">${project.projectGroup.id}</s:param>
            </s:url>
            <c1:data label="%{getText('projectView.project.group')}" name="project.projectGroup.name" valueLink="%{'${projectGroupSummaryUrl}'}"/>
            <c1:data label="%{getText('projectView.project.lastBuildDateTime')}" name="lastBuildDateTime" />
          </table>

          <redback:ifAuthorized permission="continuum-modify-group" resource="${project.projectGroup.name}">
          <div class="functnbar3">
            <table>
              <tbody>
              <tr>
                <td>
                  <form action="projectEdit.action" method="post">
                    <input type="hidden" name="projectId" value="<s:property value="project.id"/>"/>
                    <input type="submit" name="edit-project" value="<s:text name="edit"/>"/>
                  </form>
                </td>
                <td>
                  <form method="post" action="buildProject.action">
                    <input type="hidden" name="projectId" value="<s:property value="project.id"/>"/>
                    <input type="hidden" name="fromProjectPage" value="true"/>
                    <input type="submit" name="build-project" value="<s:text name="summary.buildNow"/>"/>
                  </form>
                </td>
              </tr>
              </tbody>
            </table>
          </div>
          </redback:ifAuthorized>
        </div>

        <h3><s:text name="projectView.buildDefinitions"/></h3>

        <s:action name="buildDefinitionSummary" id="summary" namespace="component" executeResult="true">
          <s:param name="projectId">${project.id}</s:param>
          <s:param name="projectGroupId">${project.projectGroup.id}</s:param>
        </s:action>

        <div class="functnbar3">
           <redback:ifAuthorized permission="continuum-modify-group" resource="${project.projectGroup.name}">
          <s:form action="buildDefinition" method="post">
            <input type="hidden" name="projectId" value="<s:property value="project.id"/>"/>
            <input type="hidden" name="projectGroupId" value="<s:property value="project.projectGroup.id"/>"/>
            <s:submit value="%{getText('add')}" theme="simple"/>
          </s:form>
          </redback:ifAuthorized>
        </div>

        <h3><s:text name="projectView.notifiers"/></h3>
        <c:if test="${not empty project.notifiers}">
          <s:set name="notifiers" value="project.notifiers" scope="request"/>
          <ec:table items="notifiers"
                    var="notifier"
                    showExports="false"
                    showPagination="false"
                    showStatusBar="false"
                    filterable="false"
                    sortable="false">
            <ec:row>
              <ec:column property="type" title="projectView.notifier.type"/>
              <ec:column property="recipient" title="projectView.notifier.recipient" cell="org.apache.maven.continuum.web.view.projectview.NotifierRecipientCell"/>
              <ec:column property="events" title="projectView.notifier.events" cell="org.apache.maven.continuum.web.view.projectview.NotifierEventCell"/>
              <ec:column property="from" title="projectView.notifier.from" cell="org.apache.maven.continuum.web.view.projectview.NotifierFromCell"/>
              <ec:column property="editAction" title="&nbsp;" width="1%">
                <redback:ifAuthorized permission="continuum-modify-group" resource="${project.projectGroup.name}">
                  <c:choose>
                    <c:when test="${!pageScope.notifier.fromProject}">
                      <s:url id="editUrl" action="editProjectNotifier" namespace="/" includeParams="none">
                        <s:param name="notifierId">${notifier.id}</s:param>
                        <s:param name="projectId" value="project.id"/>
                        <s:param name="projectGroupId">${project.projectGroup.id}</s:param>
                        <s:param name="notifierType">${notifier.type}</s:param>
                      </s:url>
                      <s:a href="%{editUrl}">
                        <img src="<s:url value='/images/edit.gif' includeParams="none"/>" alt="<s:text name="edit"/>" title="<s:text name="edit"/>" border="0">
                      </s:a>
                    </c:when>
                    <c:otherwise>
                      <img src="<s:url value='/images/edit_disabled.gif' includeParams="none"/>" alt="<s:text name='edit'/>" title="<s:text name='edit'/>" border="0" />
                    </c:otherwise>
                </c:choose>
                </redback:ifAuthorized>
                <redback:elseAuthorized>
                  <img src="<s:url value='/images/edit_disabled.gif' includeParams="none"/>" alt="<s:text name='edit'/>" title="<s:text name='edit'/>" border="0" />
                </redback:elseAuthorized>
              </ec:column>
              <ec:column property="deleteAction" title="&nbsp;" width="1%">
                <redback:ifAuthorized permission="continuum-modify-group" resource="${project.projectGroup.name}">
                  <c:choose>
                    <c:when test="${!pageScope.notifier.fromProject}">
                      <s:url id="removeUrl" action="deleteProjectNotifier!default.action" namespace="/">
                        <s:param name="projectId" value="project.id"/>
                        <s:param name="projectGroupId">${project.projectGroup.id}</s:param>
                        <s:param name="notifierType">${notifier.type}</s:param>
                        <s:param name="notifierId">${notifier.id}</s:param>
                    </s:url>
                    <s:a href="%{removeUrl}">
                      <img src="<s:url value='/images/delete.gif' includeParams="none"/>" alt="<s:text name="delete"/>" title="<s:text name="delete"/>" border="0">
                    </s:a>
                    </c:when>
                    <c:otherwise>
                      <img src="<s:url value='/images/delete_disabled.gif' includeParams="none"/>" alt="<s:text name='edit'/>" title="<s:text name='edit'/>" border="0" />
                    </c:otherwise>
                  </c:choose>
                </redback:ifAuthorized>
                <redback:elseAuthorized>
                  <img src="<s:url value='/images/delete_disabled.gif' includeParams="none"/>" alt="<s:text name='edit'/>" title="<s:text name='edit'/>" border="0" />
                </redback:elseAuthorized>
              </ec:column>
            </ec:row>
          </ec:table>
        </c:if>
        <div class="functnbar3">
           <redback:ifAuthorized permission="continuum-modify-group" resource="${project.projectGroup.name}">
          <s:form action="addProjectNotifier!default.action" method="post">
            <input type="hidden" name="projectId" value="<s:property value="project.id"/>"/>
            <input type="hidden" name="projectGroupId" value="<s:property value="project.projectGroup.id"/>"/>
            <s:submit value="%{getText('add')}" theme="simple"/>
          </s:form>
          </redback:ifAuthorized>
        </div>

        <h3><s:text name="projectView.dependencies"/></h3>
        <s:set name="dependencies" value="project.dependencies" scope="request"/>
        <ec:table items="dependencies"
                  var="dep"
                  showExports="false"
                  showPagination="false"
                  showStatusBar="false"
                  filterable="false"
                  sortable="false">
          <ec:row>
            <ec:column property="groupId" title="projectView.dependency.groupId"/>
            <ec:column property="artifactId" title="projectView.dependency.artifactId"/>
            <ec:column property="version" title="projectView.dependency.version"/>
          </ec:row>
        </ec:table>

        <h3><s:text name="projectView.developers"/></h3>
        <s:set name="developers" value="project.developers" scope="request"/>
        <ec:table items="developers"
                  showExports="false"
                  showPagination="false"
                  showStatusBar="false"
                  filterable="false"
                  sortable="false">
          <ec:row>
            <ec:column property="name" title="projectView.developer.name"/>
            <ec:column property="email" title="projectView.developer.email"/>
          </ec:row>
        </ec:table>

      </div>
    </body>
  </s:i18n>
</html>
