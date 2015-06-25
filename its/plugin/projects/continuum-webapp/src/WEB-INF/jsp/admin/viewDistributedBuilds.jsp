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
<%@ taglib uri="http://plexus.codehaus.org/redback/taglib-1.0" prefix="redback" %>

<html>
  <s:i18n name="localization.Continuum">
    <head>
      <title><s:text name="distributedBuilds.page.title"/></title>
      <meta http-equiv="refresh" content="60"/>
    </head>
    <body>
      <c:if test="${!empty actionErrors}">
        <div class="errormessage">
          <s:iterator value="actionErrors">
            <p><s:text name="<s:property/>" /></p>
          </s:iterator>
        </div>
      </c:if>
      
      <s:form id="buildForm" action="none" method="post">
        <div id="h3">
          <h3><s:text name="distributedBuilds.currentBuild.section.title"/></h3>
          <c:if test="${not empty currentDistributedBuilds}">
            <s:set name="currentDistributedBuilds" value="currentDistributedBuilds" scope="request"/>
            <ec:table items="currentDistributedBuilds"
                      var="currentBuild"
                      showExports="false"
                      showPagination="false"
                      showStatusBar="false"
                      sortable="false"
                      filterable="false">
              <ec:row>
                <ec:column property="projectUrl" title="distributedBuild.table.projectName">
                  <s:url id="viewUrl" action="buildResults">
                    <s:param name="projectId">${pageScope.currentBuild.projectId}</s:param>
                  </s:url>
                  <s:a href="%{viewUrl}">${pageScope.currentBuild.projectName}</s:a>
                </ec:column>
                <ec:column property="buildDefinitionLabel" title="distributedBuild.table.buildDefinitionLabel"/>
                <ec:column property="projectGroupName" title="distributedBuild.table.projectGroupName"/>
                <ec:column property="buildAgentUrl" title="distributedBuild.table.buildAgentUrl"/>
                <ec:column property="cancelEntry" title="&nbsp;" width="1%">
                  <s:url id="cancelUrl" action="cancelDistributedBuild" method="cancelDistributedBuild" namespace="/">
                    <s:param name="buildAgentUrl">${pageScope.currentBuild.buildAgentUrl}</s:param>
                  </s:url>
                  <redback:ifAuthorized permission="continuum-manage-queues">
                    <s:a href="%{cancelUrl}"><img src="<s:url value='/images/cancelbuild.gif' includeParams="none"/>" alt="<s:text name='cancel'/>" title="<s:text name='cancel'/>" border="0"></s:a>
                  </redback:ifAuthorized>
                  <redback:elseAuthorized>
                    <img src="<s:url value='/images/cancelbuild_disabled.gif' includeParams="none"/>" alt="<s:text name='cancel'/>" title="<s:text name='cancel'/>" border="0">
                  </redback:elseAuthorized>
                </ec:column>
              </ec:row>
            </ec:table>
          </c:if>
          <c:if test="${empty currentDistributedBuilds}">
            <s:text name="distributedBuilds.no.currentTasks"/>
          </c:if>
        </div>
      </s:form>
      <s:form id="removeBuildForm" action="removeDistributedBuildEntries.action" method="post">
        <div id="h3">
          <h3>
            <s:text name="distributedBuilds.buildQueue.section.title"/>
          </h3>
          <c:if test="${not empty distributedBuildQueues}">
            <s:set name="distributedBuildQueues" value="distributedBuildQueues" scope="request"/>
            <ec:table items="distributedBuildQueues"
                      var="buildQueue"
                      showExports="false"
                      showPagination="false"
                      showStatusBar="false"
                      sortable="false"
                      filterable="false">
              <ec:row>
                <redback:ifAuthorized permission="continuum-manage-queues">
                  <ec:column alias="selectedBuildTaskHashCodes" title=" " style="width:5px" filterable="false" sortable="false" headerCell="selectAll">
                    <input type="checkbox" name="selectedBuildTaskHashCodes" value="${pageScope.buildQueue.hashCode}" />
                  </ec:column>              
                </redback:ifAuthorized>
                <ec:column property="projectUrl" title="distributedBuild.table.projectName">
                  <s:url id="viewUrl" action="buildResults">
                    <s:param name="projectId">${pageScope.buildQueue.projectId}</s:param>
                  </s:url>
                  <s:a href="%{viewUrl}">${pageScope.buildQueue.projectName}</s:a>
                </ec:column>
                <ec:column property="buildDefinitionLabel" title="distributedBuild.table.buildDefinitionLabel"/>
                <ec:column property="projectGroupName" title="distributedBuild.table.projectGroupName"/>
                <ec:column property="buildAgentUrl" title="distributedBuild.table.buildAgentUrl"/>
                <ec:column property="cancelEntry" title="&nbsp;" width="1%">
                  <redback:ifAuthorized permission="continuum-manage-queues">
                    <s:url id="cancelUrl" action="removeDistributedBuildEntry" method="removeDistributedBuildEntry" namespace="/">
                      <s:param name="projectId">${pageScope.buildQueue.projectId}</s:param>
                      <s:param name="buildDefinitionId">${pageScope.buildQueue.buildDefinitionId}</s:param>
                      <s:param name="buildAgentUrl">${pageScope.buildQueue.buildAgentUrl}</s:param>
                    </s:url>
                    <s:a href="%{cancelUrl}"><img src="<s:url value='/images/cancelbuild.gif' includeParams="none"/>" alt="<s:text name='cancel'/>" title="<s:text name='cancel'/>" border="0"></s:a>
                  </redback:ifAuthorized>
                  <redback:elseAuthorized>
                    <img src="<s:url value='/images/cancelbuild_disabled.gif' includeParams="none"/>" alt="<s:text name='cancel'/>" title="<s:text name='cancel'/>" border="0">
                  </redback:elseAuthorized>
                </ec:column>
              </ec:row>
            </ec:table>
          </c:if>
        </div>
        <c:if test="${not empty distributedBuildQueues}">
          <div class="functnbar3">
            <table>
              <tbody>
                <tr>
                  <td>
                    <input type="button" name="remove-build-queues" value="<s:text name="distributedBuilds.removeEntries"/>" onclick="document.forms.removeBuildForm.submit();" /> 
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </c:if>
        <c:if test="${empty distributedBuildQueues}">
          <s:text name="distributedBuilds.empty"/>
        </c:if>
      </s:form>
      <s:form id="prepareForm" action="none" method="post">
        <div id="h3">
          <h3><s:text name="distributedBuilds.currentPrepareBuild.section.title"/></h3>
          <c:if test="${not empty currentDistributedPrepareBuilds}">
            <s:set name="currentDistributedPrepareBuilds" value="currentDistributedPrepareBuilds" scope="request"/>
            <ec:table items="currentDistributedPrepareBuilds"
                      var="currentPrepareBuild"
                      showExports="false"
                      showPagination="false"
                      showStatusBar="false"
                      sortable="false"
                      filterable="false">
              <ec:row>
                <ec:column property="projectGroupUrl" title="distributedPrepareBuild.table.projectGroupName">
                  <s:url id="viewUrl" action="projectGroupSummary">
                    <s:param name="projectGroupId">${pageScope.currentPrepareBuild.projectGroupId}</s:param>
                  </s:url>
                  <s:a href="%{viewUrl}">${pageScope.currentPrepareBuild.projectGroupName}</s:a>
                </ec:column>
                <ec:column property="scmRootAddress" title="distributedPrepareBuild.table.scmRootAddress"/>
                <ec:column property="buildAgentUrl" title="distributedPrepareBuild.table.buildAgentUrl"/>
              </ec:row>
            </ec:table>
          </c:if>
          <c:if test="${empty currentDistributedPrepareBuilds}">
            <s:text name="distributedPrepareBuilds.no.currentTasks"/>
          </c:if>
        </div>
      </s:form>
      
      <s:form id="removePrepareBuildForm" action="removeDistributedPrepareBuildEntries.action" method="post">
        <div id="h3">
          <h3>
            <s:text name="distributedBuilds.prepareBuildQueue.section.title"/>
          </h3>
          <c:if test="${not empty distributedPrepareBuildQueues}">
            <s:set name="distributedPrepareBuildQueues" value="distributedPrepareBuildQueues" scope="request"/>
            <ec:table items="distributedPrepareBuildQueues"
                      var="prepareBuildQueue"
                      showExports="false"
                      showPagination="false"
                      showStatusBar="false"
                      sortable="false"
                      filterable="false">
              <ec:row>
                <redback:ifAuthorized permission="continuum-manage-queues">
                  <ec:column alias="selectedPrepareBuildTaskHashCodes" title="&nbsp;" style="width:5px" filterable="false" sortable="false" width="1%" headerCell="selectAll">
                    <input type="checkbox" name="selectedPrepareBuildTaskHashCodes" value="${pageScope.prepareBuildQueue.hashCode}" />
                  </ec:column>              
                </redback:ifAuthorized>
                <ec:column property="projectGroupUrl" title="distributedPrepareBuild.table.projectGroupName">
                  <s:url id="viewUrl" action="projectGroupSummary">
                    <s:param name="projectGroupId">${pageScope.prepareBuildQueue.projectGroupId}</s:param>
                  </s:url>
                  <s:a href="%{viewUrl}">${pageScope.prepareBuildQueue.projectGroupName}</s:a>
                </ec:column>
                <ec:column property="scmRootAddress" title="distributedPrepareBuild.table.scmRootAddress"/>
                <ec:column property="buildAgentUrl" title="distributedPrepareBuild.table.buildAgentUrl"/>
                <ec:column property="cancelEntry" title="&nbsp;" width="1%">
                  <redback:ifAuthorized permission="continuum-manage-queues">
                    <s:url id="cancelUrl" action="removeDistributedPrepareBuildEntry" method="removeDistributedPrepareBuildEntry" namespace="/">
                      <s:param name="projectGroupId">${pageScope.prepareBuildQueue.projectGroupId}</s:param>
                      <s:param name="scmRootId">${pageScope.prepareBuildQueue.scmRootId}</s:param>
                      <s:param name="buildAgentUrl">${pageScope.prepareBuildQueue.buildAgentUrl}</s:param>
                    </s:url>
                    <s:a href="%{cancelUrl}"><img src="<s:url value='/images/cancelbuild.gif' includeParams="none"/>" alt="<s:text name='cancel'/>" title="<s:text name='cancel'/>" border="0"></s:a>
                  </redback:ifAuthorized>
                  <redback:elseAuthorized>
                    <img src="<s:url value='/images/cancelbuild_disabled.gif' includeParams="none"/>" alt="<s:text name='cancel'/>" title="<s:text name='cancel'/>" border="0">
                  </redback:elseAuthorized>
                </ec:column>
              </ec:row>
            </ec:table>
          </c:if>
        </div>
        <c:if test="${not empty distributedPrepareBuildQueues}">
          <div class="functnbar3">
            <table>
              <tbody>
                <tr>
                  <td>
                    <input type="button" name="remove-prepare-build-queues" value="<s:text name="distributedPrepareBuilds.removeEntries"/>" onclick="document.forms.removePrepareBuildForm.submit();" /> 
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </c:if>
        <c:if test="${empty distributedPrepareBuildQueues}">
          <s:text name="distributedPrepareBuilds.empty"/>
        </c:if>
      </s:form>
      
    </body>
  </s:i18n>
</html>
