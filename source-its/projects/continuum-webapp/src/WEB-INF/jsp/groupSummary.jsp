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
<%@ taglib uri="http://plexus.codehaus.org/redback/taglib-1.0" prefix="redback" %>

<html>
<s:i18n name="localization.Continuum">
  <head>
    <title><s:text name="groups.page.title"/></title>
    <meta http-equiv="refresh" content="300"/>
  </head>

  <body>
  <div id="h3">

    <s:if test="infoMessage != null">
       <p>${infoMessage}</p>
    </s:if>
    <s:else>
       <h3><s:text name="groups.page.section.title"/></h3>
    </s:else>
  
    <c:if test="${!empty actionErrors}">
      <div class="errormessage">
        <s:iterator value="actionErrors">
          <p><s:property/></p>
        </s:iterator>
      </div>
    </c:if>

    <c:if test="${empty groups}">
      <s:text name="groups.page.list.empty"/>
    </c:if>

    <c:if test="${not empty groups}">

    <ec:table items="groups"
              var="group"
              showExports="false"
              showPagination="false"
              showStatusBar="false"
              sortable="false"
              filterable="false">
      <ec:row highlightRow="true">
        <ec:column property="name" title="groups.table.name" width="40%" style="white-space: nowrap">
          <a href="<s:url  action="projectGroupSummary" namespace="/"><s:param name="projectGroupId">${group.id}</s:param></s:url>">${group.name}</a>
        </ec:column>
        <ec:column property="groupId" title="groups.table.groupId" width="40%"/>
        <ec:column property="buildGroupNowAction" title="&nbsp;" width="1%">
          <redback:ifAuthorized permission="continuum-build-group" resource="${group.name}">
            <s:url id="buildProjectGroupUrl" action="buildProjectGroup" namespace="/" includeParams="none">
              <s:param name="projectGroupId">${group.id}</s:param>
              <s:param name="buildDefinitionId" value="-1"/>
              <s:param name="fromSummaryPage" value="true"/>
            </s:url>
            <s:a href="%{buildProjectGroupUrl}">
              <img src="<s:url value='/images/buildnow.gif'/>" alt="<s:text name="projectGroup.buildGroup"/>" title="<s:text name="projectGroup.buildGroup"/>" border="0">
            </s:a>
          </redback:ifAuthorized>
          <redback:elseAuthorized>
            <img src="<s:url value='/images/buildnow_disabled.gif'/>" alt="<s:text name="projectGroup.buildGroup"/>" title="<s:text name="projectGroup.buildGroup"/>" border="0">
          </redback:elseAuthorized>
        </ec:column>
        <ec:column property="releaseProjectGroupAction" title="&nbsp;" width="1%">
          <redback:ifAuthorized permission="continuum-build-group" resource="${group.name}">
            <s:url id="releaseProjectGroupUrl" action="releaseProjectGroup" namespace="/" includeParams="none">
              <s:param name="projectGroupId">${group.id}</s:param>
            </s:url>
            <s:a href="%{releaseProjectGroupUrl}">
              <img src="<s:url value='/images/releaseproject.gif'/>" alt="<s:text name="projectGroup.releaseNow"/>" title="<s:text name="projectGroup.releaseNow"/>" border="0">
            </s:a>
          </redback:ifAuthorized>
          <redback:elseAuthorized>
            <img src="<s:url value='/images/releaseproject_disabled.gif'/>" alt="<s:text name="projectGroup.releaseNow"/>" title="<s:text name="projectGroup.releaseNow"/>" border="0">
          </redback:elseAuthorized>
        </ec:column>
        <ec:column property="removeProjectGroupAction" title="&nbsp;" width="1%">
          <redback:ifAuthorized permission="continuum-remove-group" resource="${group.name}">
            <s:url id="removeProjectGroupUrl" action="removeProjectGroup" namespace="/" includeParams="none">
              <s:param name="projectGroupId">${group.id}</s:param>
            </s:url>
            <s:a href="%{removeProjectGroupUrl}">
              <img src="<s:url value='/images/delete.gif'/>" alt="<s:text name="projectGroup.deleteGroup"/>" title="<s:text name="projectGroup.deleteGroup"/>" border="0">
            </s:a>
          </redback:ifAuthorized>
          <redback:elseAuthorized>
            <img src="<s:url value='/images/delete_disabled.gif'/>" alt="<s:text name="projectGroup.deleteGroup"/>" title="<s:text name="projectGroup.deleteGroup"/>" border="0">
          </redback:elseAuthorized>
        </ec:column>
        <ec:column property="numSuccesses" title="&nbsp;" format="0" width="2%" style="text-align: right" headerClass="calcHeaderSucces" calc="total" calcTitle="groups.table.summary"/>
        <ec:column property="numFailures" title="&nbsp;" format="0" width="2%" style="text-align: right" headerClass="calcHeaderFailure" calc="total" />
        <ec:column property="numErrors" title="&nbsp;" format="0" width="2%" style="text-align: right" headerClass="calcHeaderError" calc="total"/>
        <ec:column property="numProjects" title="groups.table.totalProjects" format="0" width="1%" style="text-align: right" headerStyle="text-align: center" calc="total"/>
      </ec:row>
    </ec:table>
    </c:if>
    <redback:ifAuthorized permission="continuum-add-group">
      <div class="functnbar3">
        <table>
          <tr>
            <td>
              <form action="<s:url  action='addProjectGroup' method='input' namespace='/' />" method="post">
                <input type="submit" name="addProjectGroup" value="<s:text name="projectGroup.add.section.title"/>"/>
              </form>
            </td>
          </tr>
        </table>
      </div>
    </redback:ifAuthorized>
  </div>
  </body>
</s:i18n>
</html>
