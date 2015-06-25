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

<html>
  <s:i18n name="localization.Continuum">
    <head>
      <title><s:text name="releases.page.title"/></title>
    </head>
    <body>
      <div id="h3">
        <h3><s:text name="releases.section.title"/></h3>
        <c:if test="${!empty actionErrors}">
          <div class="errormessage">
            <s:iterator value="actionErrors">
              <p><s:text name="<s:property/>" /></p>
            </s:iterator>
          </div>
        </c:if>
        <s:set name="releasesSummary" value="releasesSummary" scope="request"/>
        <ec:table items="releasesSummary"
                  var="releaseSummary"
                  showExports="false"
                  showPagination="false"
                  showStatusBar="false"
                  sortable="false"
                  filterable="false">
          <ec:row>
            <ec:column property="releaseId" title="releaseSummary.table.releaseId">
              <s:url id="viewReleaseUrl" action="releaseInProgress">
                <s:param name="releaseId">${pageScope.releaseSummary.releaseId}</s:param>
                <s:param name="projectId">${pageScope.releaseSummary.projectId}</s:param>
                <s:param name="releaseGoal">${pageScope.releaseSummary.releaseGoal}</s:param>
              </s:url>
              <s:a href="%{viewReleaseUrl}">${pageScope.releaseSummary.releaseId}</s:a>
            </ec:column>
            <ec:column property="releaseGoal" title="releaseSummary.table.releaseGoal"/>
            <ec:column property="buildAgentUrl" title="releaseSummary.table.buildAgentUrl"/>
          </ec:row>
        </ec:table>
      </div>
    </body>
  </s:i18n>
</html>