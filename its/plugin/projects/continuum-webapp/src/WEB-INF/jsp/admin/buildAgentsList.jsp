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
      <title><s:text name="buildAgents.page.title"/></title>
    </head>
    <body>
      <div id="h3">
        <h3><s:text name="buildAgents.section.title"/></h3>
        <c:if test="${!empty actionErrors}">
          <div class="errormessage">
            <s:iterator value="actionErrors">
              <p><s:property/></p>
            </s:iterator>
          </div>
        </c:if>
        <s:set name="buildAgents" value="buildAgents" scope="request"/>
        <ec:table items="buildAgents"
                  var="buildAgent"
                  showExports="false"
                  showPagination="false"
                  showStatusBar="false"
                  sortable="false"
                  filterable="false">
          <ec:row>
            <ec:column property="url" title="buildAgents.table.url">
              <s:url id="viewBuildAgentUrl" action="viewBuildAgent">
                <s:param name="buildAgent.url">${pageScope.buildAgent.url}</s:param>
              </s:url>
              <s:a href="%{viewBuildAgentUrl}">${pageScope.buildAgent.url}</s:a>
            </ec:column>
            <ec:column property="enabled" title="buildAgents.table.enabled"/>
            <ec:column property="description" title="buildAgents.table.description"/>
            <ec:column property="editActions" title="&nbsp;" width="1%">
              <s:url id="editBuildAgentUrl" action="editBuildAgent">
                <s:param name="buildAgent.url">${pageScope.buildAgent.url}</s:param>
              </s:url>
              <s:a href="%{editBuildAgentUrl}">
                <img src="<s:url value='/images/edit.gif' includeParams="none"/>" alt="<s:text name='edit'/>" title="<s:text name='edit'/>" border="0"/>
              </s:a>
            </ec:column>
            <ec:column property="deleteActions" title="&nbsp;" width="1%">
              <s:url id="removeBuildAgentUrl" action="deleteBuildAgent">
                <s:param name="buildAgent.url">${pageScope.buildAgent.url}</s:param>
              </s:url>
              <s:a href="%{removeBuildAgentUrl}">
                <img src="<s:url value='/images/delete.gif' includeParams="none"/>" alt="<s:text name='delete'/>" title="<s:text name='delete'/>" border="0"/>
              </s:a>
            </ec:column>
          </ec:row>
        </ec:table>
      </div>
      <div class="functnbar3">
        <s:form name="addBuildAgent" action="editBuildAgent" method="post">
          <s:submit value="%{getText('add')}" theme="simple"/>
        </s:form>
      </div>
      <div id="h3">
        <h3><s:text name="buildAgentGroups.section.title"/></h3>
        <s:set name="buildAgentGroups" value="buildAgentGroups" scope="request"/>
        <ec:table items="buildAgentGroups"
                  var="buildAgentGroup"
                  showExports="false"
                  showPagination="false"
                  showStatusBar="false"
                  sortable="false"
                  filterable="false">
          <ec:row>
            <ec:column property="name" title="buildAgentGroups.table.name"></ec:column>            
            <ec:column property="Agents" title="buildAgentGroups.table.agents" style="white-space: nowrap">
              <ul>                
                <c:if test="${!empty buildAgentGroup.buildAgents}" >                                 
                  <c:forEach var="envVar" items="${buildAgentGroup.buildAgents}"> 
                    <li><c:out value="${envVar.url}" /></li>
                  </c:forEach>
                </c:if>
              </ul>
            </ec:column>
            <ec:column property="editActions" title="&nbsp;" width="1%">
              <s:url id="editBuildAgentGroupUrl" action="editBuildAgentGroup">
                <s:param name="buildAgentGroup.name">${pageScope.buildAgentGroup.name}</s:param>
              </s:url>
              <s:a href="%{editBuildAgentGroupUrl}">
                <img src="<s:url value='/images/edit.gif' includeParams="none"/>" alt="<s:text name='edit'/>" title="<s:text name='edit'/>" border="0"/>
              </s:a>
            </ec:column>
            <ec:column property="deleteActions" title="&nbsp;" width="1%">
              <s:url id="removeBuildAgentGroupUrl" action="deleteBuildAgentGroup">
                <s:param name="buildAgentGroup.name">${pageScope.buildAgentGroup.name}</s:param>
              </s:url>
              <s:a href="%{removeBuildAgentGroupUrl}">
                <img src="<s:url value='/images/delete.gif' includeParams="none"/>" alt="<s:text name='delete'/>" title="<s:text name='delete'/>" border="0"/>
              </s:a>
            </ec:column>
          </ec:row>
        </ec:table>
      </div>
      <div class="functnbar3">
        <s:form name="addBuildAgentGroup" action="editBuildAgentGroup" method="post">
          <s:submit value="%{getText('add')}" theme="simple"/>
        </s:form>
      </div>
    </body>
  </s:i18n>
</html>