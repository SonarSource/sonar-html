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

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="continuum" prefix="c1" %>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<%@ taglib uri="http://plexus.codehaus.org/redback/taglib-1.0" prefix="redback" %>

<html>
  <s:i18n name="localization.Continuum">
    <head>
      <title><s:text name="buildQueue.page.title"/></title>
      <meta http-equiv="refresh" content="60"/>
    </head>
    <body>
      <s:form id="buildQueueForm" action="none" method="post">
        <div id="h3">
          <h3>
            <s:text name="buildQueue.currentTask.section.title"/>
          </h3>
          <c:if test="${not empty currentBuildProjectTasks}">
            <s:set name="currentBuildProjectTasks" value="currentBuildProjectTasks" scope="request"/>
            <ec:table items="currentBuildProjectTasks"
                      var="queue"
                      showExports="false"
                      showPagination="false"
                      showStatusBar="false"
                      sortable="false"
                      filterable="false">
              <ec:row>
                <ec:column property="name" title="buildQueue.currentTask.buildQueue" width="29%"/>
                <ec:column property="projectUrl" title="buildQueue.currentTask.projectName" width="50%">
                  <s:url id="viewUrl" action="buildResults">
                    <s:param name="projectId">${queue.task.projectId}</s:param>
                  </s:url>
                  <s:a href="%{viewUrl}">${queue.task.projectName}</s:a>
                </ec:column>
                <ec:column property="task.buildDefinitionLabel" title="buildQueue.currentTask.buildDefinition" width="19%"/>
                <ec:column property="cancelAction" title="&nbsp;" width="1%">
                  <redback:ifAuthorized permission="continuum-manage-queues">
                    <s:url id="cancelUrl" action="cancelCurrentBuildTask" method="cancelCurrent" namespace="/">
                      <s:param name="projectId">${queue.task.projectId}</s:param>
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
          <c:if test="${empty currentBuildProjectTasks}">
            <s:text name="buildQueue.no.currentTaks" />
          </c:if>
        </div>
      </s:form>
      
      <s:form id="removeBuildForm" action="removeBuildQueueEntries!removeBuildEntries.action" method="post">
        <div id="h3">
          <h3>
            <s:text name="buildQueue.section.title"/>
          </h3>
          <c:if test="${not empty buildsInQueue}">
            <s:set name="buildsInQueue" value="buildsInQueue" scope="request"/>
            <ec:table items="buildsInQueue"
                      var="queue"
                      showExports="false"
                      showPagination="false"
                      showStatusBar="false"
                      sortable="false"
                      filterable="false">
              <ec:row>
                <redback:ifAuthorized permission="continuum-manage-queues">
                  <ec:column alias="selectedBuildTaskHashCodes" title=" " style="width:5px" filterable="false" sortable="false" headerCell="selectAll">
                    <input type="checkbox" name="selectedBuildTaskHashCodes" value="${queue.task.hashCode}" />
                  </ec:column>
                </redback:ifAuthorized>
                <ec:column property="name" title="buildQueue.currentTask.buildQueue" width="29%"/>
                <ec:column property="projectUrl" title="buildQueue.currentTask.projectName" width="50%">
                  <s:url id="viewUrl" action="buildResults">
                    <s:param name="projectId">${queue.task.projectId}</s:param>
                  </s:url>
                  <s:a href="%{viewUrl}">${queue.task.projectName}</s:a>
                </ec:column>
                <ec:column property="task.buildDefinitionLabel" title="buildQueue.currentTask.buildDefinition" width="19%"/>
                <ec:column property="cancelAction" title="&nbsp;" width="1%">
                  <redback:ifAuthorized permission="continuum-manage-queues">
                    <s:url id="cancelUrl" action="removeBuildQueueEntry" method="remove" namespace="/">
                      <s:param name="projectId">${queue.task.projectId}</s:param>
                      <s:param name="buildDefinitionId">${queue.task.buildDefinitionId}</s:param>
                      <s:param name="trigger">${queue.task.buildTrigger.trigger}</s:param>
                      <s:param name="projectName">${queue.task.projectName}</s:param>
                      <s:param name="projectGroupId">${queue.task.projectGroupId}</s:param>
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
          <c:if test="${empty buildsInQueue}">
            <s:text name="buildQueue.empty"/>
          </c:if>
        </div>
        <c:if test="${not empty buildsInQueue}">
          <div class="functnbar3">
            <table>
              <tbody>
                <tr>
                  <td>
                    <input type="button" value="<s:text name="buildQueue.removeEntries"/>" onclick="document.forms.removeBuildForm.submit();" />
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </c:if>
      </s:form>

      <s:form id="checkoutForm" action="none" method="post">
        <%-- checkout queue --%>
        <div id="h3">
          <h3>
            <s:text name="checkoutQueue.currentTask.section.title"/>
          </h3>
          <c:if test="${not empty currentCheckoutTasks}">
            <s:set name="currentCheckoutTasks" value="currentCheckoutTasks" scope="request"/>
            <ec:table items="currentCheckoutTasks"
                      var="queue"
                      showExports="false"
                      showPagination="false"
                      showStatusBar="false"
                      sortable="false"
                      filterable="false">
              <ec:row>
                <ec:column property="name" title="checkoutQueue.currentTask.buildQueue" width="29%"/>
                <ec:column property="projectUrl" title="checkoutQueue.currentTask.projectName" width="69%">
                  <s:url id="viewUrl" action="projectView">
                    <s:param name="projectId">${queue.task.projectId}</s:param>
                  </s:url>
                  <s:a href="%{viewUrl}">${queue.task.projectName}</s:a>
                </ec:column>
                <ec:column property="cancelAction" title="&nbsp;" width="1%">
                  <redback:ifAuthorized permission="continuum-manage-queues">
                    <s:url id="cancelUrl" action="cancelCurrentQueueTask" method="cancelCurrentCheckout" namespace="/">
                      <s:param name="projectId">${queue.task.projectId}</s:param>
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
          <c:if test="${empty currentCheckoutTasks}">
            <s:text name="checkoutQueue.no.currentTaks" />
          </c:if>
        </div>
      </s:form>
        
      <s:form id="removeCheckoutForm" action="removeCheckoutQueueEntries!removeCheckoutEntries.action" method="post">
        <div id="h3">
          <h3>
            <s:text name="checkoutQueue.section.title"/>
          </h3>
          <c:if test="${not empty checkoutsInQueue}">
            <s:set name="checkoutsInQueue" value="checkoutsInQueue" scope="request"/>
            <ec:table items="checkoutsInQueue"
                      var="queue"
                      showExports="false"
                      showPagination="false"
                      showStatusBar="false"
                      sortable="false"
                      filterable="false">
              <ec:row>
                <redback:ifAuthorized permission="continuum-manage-queues">
                  <ec:column alias="selectedCheckOutTaskHashCodes" title=" " style="width:5px" filterable="false" sortable="false" headerCell="selectAll">
                    <input type="checkbox" name="selectedCheckOutTaskHashCodes" value="${queue.task.hashCode}" />
                  </ec:column>
                </redback:ifAuthorized>
                <ec:column property="name" title="checkoutQueue.currentTask.buildQueue" width="29%"/>
                <ec:column property="projectUrl" title="checkoutQueue.currentTask.projectName" width="69%">
                  <s:url id="viewUrl" action="projectView">
                    <s:param name="projectId">${queue.task.projectId}</s:param>
                  </s:url>
                  <s:a href="%{viewUrl}">${queue.task.projectName}</s:a>
                </ec:column>
                <ec:column property="cancelAction" title="&nbsp;" width="1%">
                  <redback:ifAuthorized permission="continuum-manage-queues">
                    <s:url id="cancelUrl" action="removeCheckoutQueueEntry" method="removeCheckout" namespace="/">
                      <s:param name="projectId">${queue.task.projectId}</s:param>
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
          <c:if test="${empty checkoutsInQueue}">
            <s:text name="checkoutQueue.empty" />
          </c:if>
        </div>
        <c:if test="${not empty checkoutsInQueue}">
          <div class="functnbar3">
            <table>
              <tbody>
                <tr>
                  <td>
                    <redback:ifAuthorized permission="continuum-manage-queues">
                    <input type="submit" value="<s:text name="checkoutQueue.removeEntries"/>" onclick="document.forms.removeCheckoutForm.submit();" />
                    </redback:ifAuthorized>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </c:if>          
      </s:form>

      <s:form id="prepareBuildForm" action="none" method="post">
      	<div id="h3">
          <h3><s:text name="prepareBuildQueue.currentTask.section.title"/></h3>
          <c:if test="${not empty currentPrepareBuilds}">
            <s:set name="currentPrepareBuilds" value="currentPrepareBuilds" scope="request"/>
            <ec:table items="currentPrepareBuilds"
                      var="currentPrepareBuild"
                      showExports="false"
                      showPagination="false"
                      showStatusBar="false"
                      sortable="false"
                      filterable="false">
              <ec:row>
                <ec:column property="projectGroupUrl" title="prepareBuildQueue.table.projectGroupName">
                  <s:url id="viewUrl" action="projectGroupSummary">
                    <s:param name="projectGroupId">${pageScope.currentPrepareBuild.projectGroupId}</s:param>
                  </s:url>
                  <s:a href="%{viewUrl}">${pageScope.currentPrepareBuild.projectGroupName}</s:a>
                </ec:column>
                <ec:column property="scmRootAddress" title="prepareBuildQueue.table.scmRootAddress"/>
              </ec:row>
            </ec:table>
          </c:if>
          <c:if test="${empty currentPrepareBuilds}">
            <s:text name="prepareBuildQueue.no.currentTasks"/>
          </c:if>
        </div>
      </s:form>
       
      <s:form id="removePrepareBuildForm" action="removePrepareBuildEntries.action" method="post">
        <div id="h3">
          <h3>
            <s:text name="prepareBuildQueue.section.title"/>
          </h3>
          <c:if test="${not empty prepareBuildQueues}">
            <s:set name="prepareBuildQueues" value="prepareBuildQueues" scope="request"/>
            <ec:table items="prepareBuildQueues"
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
                <ec:column property="projectGroupUrl" title="prepareBuildQueue.table.projectGroupName">
                  <s:url id="viewUrl" action="projectGroupSummary">
                    <s:param name="projectGroupId">${pageScope.prepareBuildQueue.projectGroupId}</s:param>
                  </s:url>
                  <s:a href="%{viewUrl}">${pageScope.prepareBuildQueue.projectGroupName}</s:a>
                </ec:column>
                <ec:column property="scmRootAddress" title="prepareBuildQueue.table.scmRootAddress"/>
                <ec:column property="cancelEntry" title="&nbsp;" width="1%">
                  <redback:ifAuthorized permission="continuum-manage-queues">
                    <s:url id="cancelUrl" action="removePrepareBuildEntry" method="removePrepareBuildEntry" namespace="/">
                      <s:param name="projectGroupId">${pageScope.prepareBuildQueue.projectGroupId}</s:param>
                      <s:param name="scmRootId">${pageScope.prepareBuildQueue.scmRootId}</s:param>
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
        <c:if test="${not empty prepareBuildQueues}">
          <div class="functnbar3">
            <table>
              <tbody>
                <tr>
                  <td>
                    <input type="button" name="remove-prepare-build-queues" value="<s:text name="prepareBuildQueue.removeEntries"/>" onclick="document.forms.removePrepareBuildForm.submit();" /> 
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </c:if>
        <c:if test="${empty prepareBuildQueues}">
          <s:text name="prepareBuildQueue.empty"/>
        </c:if>
      </s:form>
    </body>
  </s:i18n>
</html>
