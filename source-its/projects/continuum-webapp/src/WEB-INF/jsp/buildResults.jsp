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
        <title><s:text name="buildResults.page.title"/></title>
        <meta http-equiv="refresh" content="300"/>
    </head>
    <body>
      <div id="h3">

        <jsp:include page="/WEB-INF/jsp/navigations/ProjectMenu.jsp">
          <jsp:param name="tab" value="buildResults"/>
        </jsp:include>

        <h3>
            <s:text name="buildResults.section.title">
                <s:param><s:property value="project.name"/></s:param>
            </s:text>
        </h3>
        
        <c:if test="${!empty actionErrors}">
          <div class="errormessage">
            <s:iterator value="actionErrors">
              <p><s:property/></p>
            </s:iterator>
          </div>
        </c:if>
    
        <form id="buildResultsForm" action="removeBuildResults.action" method="post">
          <s:set name="buildResults" value="buildResults" scope="request"/>
          <ec:table items="buildResults"
                    var="buildResult"
                    showExports="false"
                    showPagination="false"
                    showStatusBar="false"
                    filterable="false"
                    sortable="false">
            <ec:row highlightRow="true">
              <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroupName}">
                <ec:column alias="selectedBuildResults" title=" " style="width:5px" filterable="false" sortable="false" headerCell="selectAll">
                  <input type="checkbox" name="selectedBuildResults" value="${buildResult.id}" />
                </ec:column>
              </redback:ifAuthorized>
              <ec:column property="buildNumberIfNotZero" title="buildResults.buildNumber">
                  <c:if test="${pageScope.buildResult.state == 2}">
                    <c:out value="${pageScope.buildResult.buildNumber}"/>
                  </c:if>
              </ec:column>
              <ec:column property="startTime" title="buildResults.startTime" cell="date"/>
              <ec:column property="endTime" title="buildResults.endTime" cell="date"/>
              <ec:column property="duration" title="&nbsp;">
                <c:choose>
                  <c:when test="${buildResult.endTime gt 0}">
                    <s:text name="buildResults.duration"/> : ${buildResult.durationTime}
                  </c:when>
                  <c:otherwise>
                    <s:text name="buildResults.startedSince"/> : ${buildResult.elapsedTime}
                  </c:otherwise>
                </c:choose>
              </ec:column>
              <ec:column property="state" title="buildResults.state" cell="org.apache.maven.continuum.web.view.buildresults.StateCell"/>
              <ec:column property="buildDefinition.description" title="buildResults.buildDefinition.description" />
              <ec:column property="actions" title="&nbsp;">
                <s:url id="buildResultUrl" action="buildResult">
                  <s:param name="projectId">${projectId}</s:param>
                  <s:param name="projectName">${projectName}</s:param>
                  <s:param name="buildId">${buildResult.id}</s:param>
                  <s:param name="projectGroupId">${projectGroupId}</s:param>
                </s:url>
                <s:a href="%{buildResultUrl}"><s:text name="buildResults.result"/></s:a>
              </ec:column>
            </ec:row>
          </ec:table>
          <c:if test="${not empty buildResults}">
            <div class="functnbar3">
              <table>
                <tbody>
                  <tr>
                    <td>
                      <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroupName}">
                        <s:hidden name="projectGroupId"/>
                        <s:hidden name="projectId"/>
                        <input type="button" name="delete-project" value="<s:text name="delete"/>" onclick="document.forms.buildResultsForm.submit();" />
                      </redback:ifAuthorized>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </c:if>
        </form>
      </div>
    </body>
  </s:i18n>
</html>
