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
<s:i18n name="localization.Continuum">

  <h3><s:text name="projectGroupNotifierSummaryComponent.groupNotifiers"><s:param>${projectGroup.name}</s:param></s:text></h3>
  <c:if test="${not empty projectGroupNotifierSummaries}">
  <ec:table items="projectGroupNotifierSummaries"
            var="projectGroupNotifierSummary"
            showExports="false"
            showPagination="false"
            showStatusBar="false"
            filterable="false"
            sortable="false">
    <ec:row>
      <ec:column property="type" title="projectView.notifier.type"/>
      <ec:column property="recipient" title="projectView.notifier.recipient"/>
      <ec:column property="events" title="projectView.notifier.events"/>
      <!-- ec:column property="sender" title="projectView.notifier.sender"/ -->
      <ec:column property="editActions" title="&nbsp;" width="1%">
        <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroup.name}">
          <s:url id="editUrl" action="editProjectGroupNotifier" namespace="/">
            <s:param name="projectGroupId">${pageScope.projectGroupNotifierSummary.projectGroupId}</s:param>
            <s:param name="notifierId">${pageScope.projectGroupNotifierSummary.id}</s:param>
            <s:param name="notifierType">${pageScope.projectGroupNotifierSummary.type}</s:param>
          </s:url>
          <s:a href="%{editUrl}">
            <img src="<s:url value='/images/edit.gif' includeParams="none"/>" alt="<s:text name="edit"/>" title="<s:text name="edit"/>" border="0">
          </s:a>
        </redback:ifAuthorized>
        <redback:elseAuthorized>
          <img src="<s:url value='/images/edit_disabled.gif' includeParams="none"/>" alt="<s:text name="edit"/>" title="<s:text name="edit"/>" border="0">
        </redback:elseAuthorized>
      </ec:column>    
      <ec:column property="deleteActions" title="&nbsp;" width="1%">
        <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroup.name}">
          <s:url id="removeUrl" action="deleteProjectGroupNotifier!default.action" namespace="/">
            <s:param name="projectGroupId">${pageScope.projectGroupNotifierSummary.projectGroupId}</s:param>
            <s:param name="notifierId">${pageScope.projectGroupNotifierSummary.id}</s:param>
            <s:param name="notifierType">${pageScope.projectGroupNotifierSummary.type}</s:param>
          </s:url>
        <s:a href="%{removeUrl}">
          <img src="<s:url value='/images/delete.gif' includeParams="none"/>" alt="<s:text name="delete"/>" title="<s:text name="delete"/>" border="0">
        </s:a>
        </redback:ifAuthorized>
        <redback:elseAuthorized>
          <img src="<s:url value='/images/delete_disabled.gif' includeParams="none"/>" alt="<s:text name="delete"/>" title="<s:text name="delete"/>" border="0">
        </redback:elseAuthorized>
      </ec:column>      
    </ec:row>
  </ec:table>
  </c:if>

  <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroup.name}">
    <div class="functnbar3">
      <s:url id="addUrl" action="addProjectGroupNotifier" namespace="/"  includeContext="false" includeParams="none" />
      <s:form action="%{addUrl}" method="post">
        <input type="hidden" name="projectGroupId" value="<s:property value="projectGroupId"/>"/>
        <s:submit value="%{getText('add')}" theme="simple"/>
        </s:form>
    </div>
  </redback:ifAuthorized>

  <c:if test="${not empty projectNotifierSummaries}">
    <h3><s:text name="projectGroupNotifierSummaryComponent.projectNotifiers"/></h3>
    <ec:table items="projectNotifierSummaries"
              var="projectNotifierSummary"
              showExports="false"
              showPagination="false"
              showStatusBar="false"
              filterable="false"
              sortable="false">
      <ec:row>
        <ec:column property="projectName" title="projectView.project.name">
          <s:url id="projectUrl" action="projectView" namespace="/" includeParams="none">
            <s:param name="projectId">${pageScope.projectNotifierSummary.projectId}</s:param>
          </s:url>
        <s:a href="%{projectUrl}">${pageScope.projectNotifierSummary.projectName}</s:a>
        </ec:column>
        <ec:column property="type" title="projectView.notifier.type"/>
        <ec:column property="recipient" title="projectView.notifier.recipient"/>
        <ec:column property="events" title="projectView.notifier.events"/>
        <!-- ec:column property="sender" title="projectView.notifier.sender"/ -->
        <ec:column property="editActions" title="&nbsp;" width="1%">
          <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroup.name}">
            <c:choose>
              <c:when test="${!pageScope.projectNotifierSummary.fromProject}">
                <s:url id="editUrl" action="editProjectNotifier" namespace="/" includeParams="none">
                  <s:param name="projectGroupId">${pageScope.projectNotifierSummary.projectGroupId}</s:param>
                  <s:param name="projectId">${pageScope.projectNotifierSummary.projectId}</s:param>
                  <s:param name="notifierId">${pageScope.projectNotifierSummary.id}</s:param>
                  <s:param name="notifierType">${pageScope.projectNotifierSummary.type}</s:param>
                  <s:param name="fromGroupPage" value="true"/>
                </s:url>
                <s:a href="%{editUrl}">
                  <img src="<s:url value='/images/edit.gif' includeParams="none"/>" alt="<s:text name="edit"/>" title="<s:text name="edit"/>" border="0">
                </s:a>
              </c:when>
              <c:otherwise>
                <img src="<s:url value='/images/edit_disabled.gif' includeParams="none"/>" alt="<s:text name="edit"/>" title="<s:text name="edit"/>" border="0">
              </c:otherwise>
            </c:choose>
          </redback:ifAuthorized>
          <redback:elseAuthorized>
            <img src="<s:url value='/images/edit_disabled.gif' includeParams="none"/>" alt="<s:text name="edit"/>" title="<s:text name="edit"/>" border="0">
          </redback:elseAuthorized>
        </ec:column>
        <ec:column property="deleteActions" title="&nbsp;" width="1%">
          <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroup.name}">
            <c:choose>
              <c:when test="${!pageScope.projectNotifierSummary.fromProject}">
                <s:url id="removeUrl" action="deleteProjectNotifier!default.action" namespace="/">
                  <s:param name="projectGroupId">${pageScope.projectNotifierSummary.projectGroupId}</s:param>
                  <s:param name="projectId">${pageScope.projectNotifierSummary.projectId}</s:param>
                  <s:param name="notifierId">${pageScope.projectNotifierSummary.id}</s:param>
                  <s:param name="fromGroupPage" value="true"/>
                </s:url>
                <s:a href="%{removeUrl}">
                  <img src="<s:url value='/images/delete.gif' includeParams="none"/>" alt="<s:text name="delete"/>" title="<s:text name="delete"/>" border="0">
                </s:a>
              </c:when>
              <c:otherwise>
                <img src="<s:url value='/images/delete_disabled.gif' includeParams="none"/>" alt="<s:text name="delete"/>" title="<s:text name="delete"/>" border="0">
              </c:otherwise>
            </c:choose>
          </redback:ifAuthorized>
          <redback:elseAuthorized>
            <img src="<s:url value='/images/delete_disabled.gif' includeParams="none"/>" alt="<s:text name="delete"/>" title="<s:text name="delete"/>" border="0">
          </redback:elseAuthorized>
        </ec:column>
      </ec:row>
    </ec:table>
  </c:if>
</s:i18n>
