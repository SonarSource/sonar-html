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
<html>
  <s:i18n name="localization.Continuum">
    <head>
        <title><s:text name="releaseProject.page.title"/></title>
        <meta http-equiv="refresh" content="10;url=<s:url includeParams="all" />"/>
    </head>
    <body>
      <h2><s:text name="releaseInProgress.section.title"/></h2>
      <h3><s:property value="name"/></h3>
      <div class="axial">
        <table width="100%">
          <tr>
            <th><s:text name="releaseInProgress.status"/></th>
            <th width="100%"><s:text name="releaseInProgress.phase"/></th>
          </tr>
          <s:iterator value="listenerSummary.phases">
            <tr>
              <td>
              <s:if test="listenerSummary.completedPhases.contains( top )">
                <img src="<s:url value='/images/icon_success_sml.gif' includeParams="none"/>"
                     alt="<s:text name="done"/>" title="<s:text name="done"/>" border="0">
              </s:if>
              <s:elseif test="listenerSummary.inProgress.equals( top )">
                <s:if test="listenerSummary.error == null">
                  <img src="<s:url value='/images/building.gif' includeParams="none"/>"
                       alt="<s:text name="in.progress"/>" title="<s:text name="in.progress"/>" border="0">
                </s:if>
                <s:else>
                  <img src="<s:url value='/images/icon_error_sml.gif' includeParams="none"/>"
                       alt="<s:text name="error"/>" title="<s:text name="error"/>" border="0">
                </s:else>
              </s:elseif>
              <s:else>
                <img src="<s:url value='/images/inqueue.gif' includeParams="none"/>"
                     alt="<s:text name="queued"/>" title="<s:text name="queued"/>" border="0">
              </s:else>
              </td>
              <td><s:property/></td>
            </tr>
          </s:iterator>
        </table>
      </div>

      <s:form action="releaseInProgress" method="get">
        <s:hidden name="projectId"/>
        <s:hidden name="releaseId"/>
        <s:hidden name="releaseGoal"/>
        <s:submit value="%{getText('refresh')}"/>
      </s:form>
    </body>
  </s:i18n>
</html>
