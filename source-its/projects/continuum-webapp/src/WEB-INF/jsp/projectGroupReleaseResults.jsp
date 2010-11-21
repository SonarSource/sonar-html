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
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://plexus.codehaus.org/redback/taglib-1.0" prefix="redback" %>

<html>
  <s:i18n name="localization.Continuum">
    <head>
      <title><s:text name="projectGroup.page.title"/></title>
    </head>

    <body>
      <div id="h3">

      <s:action name="projectGroupTab" executeResult="true">
        <s:param name="tabName" value="'ReleaseResults'"/>
      </s:action>
    
      <h3><s:text name="projectGroup.releaseResults.section.title"><s:param>${projectGroup.name}</s:param></s:text></h3>
      
      <form id="releaseResultsForm" action="removeReleaseResults.action" method="post">
        <ec:table items="releaseResults"
                var="result"
                showExports="false"
                showPagination="false"
                showStatusBar="false"
                filterable="false"
                sortable="false">
          <ec:row highlightRow="true">
            <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroup.name}">
              <ec:column alias="selectedReleaseResults" title=" " style="width:5px" filterable="false" sortable="false" headerCell="selectAll">
                <input type="checkbox" name="selectedReleaseResults" value="${result.id}" />
              </ec:column>
            </redback:ifAuthorized>
            <ec:column property="project.name" title="releaseResults.project"/>
            <ec:column property="releaseGoal" title="releaseResults.releaseGoal"/>
            <ec:column property="startTime" title="releaseResults.startTime" cell="date"/>
            <ec:column property="endTime" title="releaseResults.endTime" cell="date"/>
            <ec:column property="resultCode" title="releaseResults.state">
              <c:choose>
                <c:when test="${pageScope.result.resultCode == 0}">
                  <s:text name="releaseViewResult.success"/>
                </c:when>
                <c:otherwise>
                  <s:text name="releaseViewResult.error"/>
                </c:otherwise>
              </c:choose>
            </ec:column>
            <ec:column property="actions" title="&nbsp;">
               <s:url id="viewReleaseResultUrl" action="viewReleaseResult">
                 <s:param name="releaseResultId">${pageScope.result.id}</s:param>
                 <s:param name="projectGroupId">${projectGroupId}</s:param>
               </s:url>
               <s:a href="%{viewReleaseResultUrl}"><s:text name="releaseResults.viewResult"/></s:a>
             </ec:column>
          </ec:row>
        </ec:table>
        <c:if test="${not empty releaseResults}">
          <div class="functnbar3">
            <table>
              <tbody>
                <tr>
                  <td>
                    <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroup.name}">
                      <s:hidden name="projectGroupId"/>
                      <input type="button" name="delete-release-results" value="<s:text name="delete"/>" onclick="document.forms.releaseResultsForm.submit();" />
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