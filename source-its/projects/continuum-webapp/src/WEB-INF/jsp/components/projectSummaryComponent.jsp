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
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ taglib uri="continuum" prefix="c1" %>
<%@ taglib uri="http://plexus.codehaus.org/redback/taglib-1.0" prefix="redback" %>

<s:i18n name="localization.Continuum">
<c:if test="${not empty projects}">

  <h3><s:text name="projectGroup.buildsStatut.title"/></h3>
  <table>
    <tr>
      <td>
          <s:text name="projectGroup.buildsStatut.success"/> : ${groupSummary.numSuccesses}
          &nbsp;<img src="<s:url value='/images/icon_success_sml.gif' includeParams="none"/>" alt="<s:text name="projectGroup.buildsStatut.success"/>">
          &nbsp; <s:text name="projectGroup.buildsStatut.errors"/> : ${groupSummary.numErrors}
          &nbsp;<img src="<s:url value='/images/icon_error_sml.gif' includeParams="none"/>" alt="<s:text name="projectGroup.buildsStatut.errors"/>">
          &nbsp; <s:text name="projectGroup.buildsStatut.failures"/> : ${groupSummary.numFailures}
          &nbsp;<img src="<s:url value='/images/icon_warning_sml.gif' includeParams="none"/>" alt="<s:text name="projectGroup.buildsStatut.failures"/>">
      <td>      
    </tr>
  </table>

  <h3><s:text name="projectGroup.projects.title"/></h3>

  <form id="projectsForm" action="ProjectsList.action" method="post">
    <input type="hidden" name="methodToCall" value="" />
    <input type="hidden" name="buildDefinitionId" value="-1" />
  <ec:table items="projects"
            var="project"
            showExports="false"
            showPagination="false"
            showStatusBar="false"
            sortable="false"
            filterable="false">
    <ec:row highlightRow="true">

      <%-- needed to access project in included pages --%>
      <c:set var="project" value="${pageScope.project}" scope="request"/>

      <%-- placed here for reusability --%>
      <c:set var="projectIdle" value="${!project.inBuildingQueue and ( ( ( project.state gt 0 ) and ( project.state lt 5 ) ) or project.state == 7 or project.state gt 8 ) }" scope="request"/>

      <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroupName}">
        <ec:column alias="selectedProjects" title=" " style="width:5px" filterable="false" sortable="false" width="1%" headerCell="selectAll">
          <input type="checkbox" name="selectedProjects" value="${project.id}" />
        </ec:column>
      </redback:ifAuthorized>
      <ec:column property="state" title="&nbsp;" width="1%" cell="org.apache.maven.continuum.web.view.StateCell"/>
      <ec:column property="name" title="summary.projectTable.name" width="50%">
        <s:url id="projectUrl" action="projectView" namespace="/" includeParams="none">
          <s:param name="projectId">${project.id}</s:param>
        </s:url>
        <s:a href="%{projectUrl}">${pageScope.project.name}</s:a>
      </ec:column>
      <ec:column property="version" title="summary.projectTable.version" width="12%"/>
      <ec:column property="buildNumber" title="summary.projectTable.build" width="2%" style="text-align: center">
        <c:choose>
          <c:when test="${project.buildNumber gt 0 && project.buildInSuccessId gt 0}">
            <redback:ifAuthorized permission="continuum-view-group" resource="${projectGroupName}">
              <s:url id="buildResult" action="buildResult">
                <s:param name="projecGroupId">${project.projectGroupId}</s:param>
                <s:param name="projectId">${project.id}</s:param>
                <s:param name="projectName">${project.name}</s:param>
                <s:param name="buildId">${project.buildInSuccessId}</s:param>
              </s:url>
              <s:a href="%{buildResult}">${project.buildNumber}</s:a>
            </redback:ifAuthorized>
            <redback:elseAuthorized>
              ${project.buildNumber}
            </redback:elseAuthorized>
          </c:when>
          <c:when test="${project.buildNumber gt 0 && project.buildInSuccessId lt 0}">
              ${project.buildNumber}
          </c:when>
          <c:otherwise>
            &nbsp;
          </c:otherwise>
        </c:choose>
      </ec:column>
      <ec:column property="lastBuildDateTime" title="summary.projectTable.lastBuildDateTime" width="30%" cell="date"/>
      <ec:column property="buildNowAction" title="&nbsp;" width="1%">
        <c:choose>
          <c:when test="${project.inBuildingQueue}">
            <img src="<s:url value='/images/inqueue.gif' includeParams="none"/>" alt="<s:text name="legend.queuedBuild"/>" title="<s:text name="legend.queuedBuild"/>" border="0">
          </c:when>
          <c:otherwise>
            <redback:ifAuthorized permission="continuum-build-group" resource="${projectGroupName}">
              <c:choose>
                <c:when test="${projectIdle}">
                  <s:url id="buildProjectUrl" action="buildProject" namespace="/" includeParams="none">
                    <s:param name="projectId">${project.id}</s:param>
                    <s:param name="projectGroupId">${project.projectGroupId}</s:param>
                    <s:param name="fromGroupPage" value="true"/>
                  </s:url>
                  <s:a href="%{buildProjectUrl}">
                    <img src="<s:url value='/images/buildnow.gif' includeParams="none"/>" alt="<s:text name="legend.buildNow"/>" title="<s:text name="legend.buildNow"/>" border="0">
                  </s:a>
                </c:when>
                <c:otherwise>
                  <s:url id="cancelBuildProjectUrl" action="cancelBuild" namespace="/" includeParams="none">
                    <s:param name="projectId">${project.id}</s:param>
                    <s:param name="projectGroupId">${project.projectGroupId}</s:param>
                  </s:url>
                  <c:choose>
                    <c:when test="${project.state != 8}">
                      <s:a href="%{cancelBuildProjectUrl}">
                        <img src="<s:url value='/images/cancelbuild.gif' includeParams="none"/>" alt="<s:text name="legend.cancelBuild"/>" title="<s:text name="legend.cancelBuild"/>" border="0">
                      </s:a>
                    </c:when>
                    <c:otherwise>
                      <img src="<s:url value='/images/cancelbuild_disabled.gif' includeParams="none"/>" alt="<s:text name="legend.cancelBuild"/>" title="<s:text name="legend.cancelBuild"/>" border="0">
                    </c:otherwise>
                  </c:choose>
                </c:otherwise>
              </c:choose>
            </redback:ifAuthorized>
            <redback:elseAuthorized>
              <c:choose>
                <c:when test="${projectIdle}">
                  <img src="<s:url value='/images/buildnow_disabled.gif' includeParams="none"/>" alt="<s:text name="legend.buildNow"/>" title="<s:text name="legend.buildNow"/>" border="0">
                </c:when>
                <c:otherwise>
                  <img src="<s:url value='/images/cancelbuild_disabled.gif' includeParams="none"/>" alt="<s:text name="legend.cancelBuild"/>" title="<s:text name="legend.cancelBuild"/>" border="0">
                </c:otherwise>
              </c:choose>
            </redback:elseAuthorized>
          </c:otherwise>
        </c:choose>
      </ec:column>
      <ec:column property="buildHistoryAction" title="&nbsp;" width="1%">
        <redback:ifAuthorized permission="continuum-view-group" resource="${projectGroupName}">
        <c:choose>
          <c:when test="${pageScope.project.latestBuildId > 0}">
            <s:url id="buildResultsUrl" action="buildResults" namespace="/">
              <s:param name="projectId">${project.id}</s:param>
              <s:param name="projectName">${project.name}</s:param>
            </s:url>
            <s:a href="%{buildResultsUrl}"><img src="<s:url value='/images/buildhistory.gif' includeParams="none"/>" alt="<s:text name="legend.buildHistory"/>" title="<s:text name="legend.buildHistory"/>" border="0"></s:a>
          </c:when>
          <c:otherwise>
            <img src="<s:url value='/images/buildhistory_disabled.gif' includeParams="none"/>" alt="<s:text name="legend.buildHistory"/>" title="<s:text name="legend.buildHistory"/>" border="0">
          </c:otherwise>
        </c:choose>
        </redback:ifAuthorized>
        <redback:elseAuthorized>
          <img src="<s:url value='/images/buildhistory_disabled.gif' includeParams="none"/>" alt="<s:text name="legend.buildHistory"/>" title="<s:text name="legend.buildHistory"/>" border="0">
        </redback:elseAuthorized>
      </ec:column>
      <ec:column property="workingCopyAction" title="&nbsp;" width="1%">
        <redback:ifAuthorized permission="continuum-view-group" resource="${projectGroupName}">
        <c:choose>
          <c:when test="${pageScope.project.state == 10 || pageScope.project.state == 2 || pageScope.project.state == 3 || pageScope.project.state == 4 || pageScope.project.state == 6}">
            <s:url id="workingCopyUrl" action="workingCopy" namespace="/">
              <s:param name="projectId">${project.id}</s:param>
            </s:url>
            <s:a href="%{workingCopyUrl}"><img src="<s:url value='/images/workingcopy.gif' includeParams="none"/>" alt="<s:text name="legend.workingCopy"/>" title="<s:text name="legend.workingCopy"/>" border="0"></s:a>
          </c:when>
          <c:otherwise>
            <img src="<s:url value='/images/workingcopy_disabled.gif' includeParams="none"/>" alt="<s:text name="legend.workingCopy"/>" title="<s:text name="legend.workingCopy"/>" border="0">
          </c:otherwise>
        </c:choose>
        </redback:ifAuthorized>
        <redback:elseAuthorized>
          <img src="<s:url value='/images/workingcopy_disabled.gif' includeParams="none"/>" alt="<s:text name="legend.workingCopy"/>" title="<s:text name="legend.workingCopy"/>" border="0">
        </redback:elseAuthorized>
      </ec:column>
      <ec:column property="releaseAction" title="&nbsp;" width="1%" sortable="false">
        <redback:ifAuthorized permission="continuum-build-group" resource="${projectGroupName}">
        <c:choose>
          <c:when test="${pageScope.project.state == 2 && pageScope.project.projectType == 'maven2'}">
            <s:url id="releaseProjectUrl" action="releasePromptGoal" namespace="/">
              <s:param name="projectId">${project.id}</s:param>
            </s:url>
            <s:a href="%{releaseProjectUrl}">
              <img src="<s:url value='/images/releaseproject.gif' includeParams="none"/>" alt="<s:text name="legend.release"/>" title="<s:text name="legend.release"/>" border="0"/>
            </s:a>
          </c:when>
          <c:otherwise>
            <img src="<s:url value='/images/releaseproject_disabled.gif' includeParams="none"/>" alt="<s:text name="legend.release"/>" title="<s:text name="legend.release"/>" border="0"/>
          </c:otherwise>
        </c:choose>
        </redback:ifAuthorized>
        <redback:elseAuthorized>
          <img src="<s:url value='/images/releaseproject_disabled.gif' includeParams="none"/>" alt="<s:text name="legend.release"/>" title="<s:text name="legend.release"/>" border="0">
        </redback:elseAuthorized>
      </ec:column>
      <ec:column property="deleteAction" title="&nbsp;" width="1%" sortable="false">
        <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroupName}">
        <c:choose>
          <c:when
              test="${projectIdle}">
            <s:url id="deleteProjectUrl" value="deleteProject!default.action" namespace="/">
              <s:param name="projectId">${project.id}</s:param>
              <s:param name="projectGroupId">${project.projectGroupId}</s:param>
            </s:url>
            <s:a href="%{deleteProjectUrl}">
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
  <c:if test="${not empty projects}">
    <div class="functnbar3">
      <table>
        <tbody>
          <tr>
            <td>
              <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroupName}">
                <input type="hidden" name="projectGroupId" value="${projectGroupId}" />
                <s:select theme="simple" name="buildDef" list="buildDefinitions"
                           listKey="value" listValue="key" headerKey="-1" headerValue="%{getText('projectGroup.buildDefinition.label')}"
                           onchange="$('projectsForm').buildDefinitionId.value=$('buildDef').value" />
                <input type="button" name="build-projects" value="<s:text name="projectGroup.buildProjects"/>" onclick="$('projectsForm').methodToCall.value='build';document.forms.projectsForm.submit();" />
                <input type="button" name="cancel-builds" value="<s:text name="projectGroup.cancelBuilds"/>" onclick="document.forms.projectsForm.action='cancelBuilds.action';document.forms.projectsForm.submit();" />
                <input type="button" name="delete-projects" value="<s:text name="projectGroup.deleteProjects"/>" onclick="document.forms.projectsForm.methodToCall.value='confirmRemove';document.forms.projectsForm.submit();" />
              </redback:ifAuthorized>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </c:if>
  </form>
</c:if>
</s:i18n>
