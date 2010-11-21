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

<html>
  <s:i18n name="localization.Continuum">
    <head>
      <title><s:text name="projectGroup.page.title"/></title>
    </head>

    <body>
      <div id="h3">

    <s:action name="projectGroupTab" executeResult="true">
      <s:param name="tabName" value="'Members'"/>
    </s:action>
    <div class="axial">
      <!--
      Scan for new Projects?
      -->
    </div>

    <h3><s:text name="projectGroup.members.section.title"><s:param>${projectGroup.name}</s:param></s:text></h3>

    <ec:table items="groupProjects"
              var="project"
              showExports="false"
              showPagination="false"
              showStatusBar="false"
              filterable="false"
              sortable="false">
      <ec:row highlightRow="true">
        <ec:column property="name" title="summary.projectTable.name" width="48%">
          <s:url id="projectViewUrl" action="projectView">
            <s:param name="projectId">${pageScope.project.id}</s:param>
          </s:url>
          <s:a href="%{projectViewUrl}">${pageScope.project.name}</s:a>
        </ec:column>
        <ec:column property="editAction" title="&nbsp;" width="1%" sortable="false">
          <center>
            <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroup.name}">
            <c:choose>
              <c:when
                  test="${pageScope.project.state == 1 || pageScope.project.state == 10 || pageScope.project.state == 2 || pageScope.project.state == 3 || pageScope.project.state == 4}">
                <s:url id="editProjectUrl" action="projectEdit">
                  <s:param name="projectId">${pageScope.project.id}</s:param>
                  <s:param name="projectName">${project.name}</s:param>
                </s:url>
                <s:a href="%{editProjectUrl}">
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
          </center>
        </ec:column>
        <ec:column property="deleteAction" title="&nbsp;" width="1%" sortable="false">
          <center>
            <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroup.name}">
            <c:choose>
              <c:when
                  test="${pageScope.project.state == 1 || pageScope.project.state == 10 || pageScope.project.state == 2 || pageScope.project.state == 3 || pageScope.project.state == 4}">
                <s:url id="removeProjectUrl" action="deleteProject!default.action">
                  <s:param name="projectId">${pageScope.project.id}</s:param>
                  <s:param name="projectName">${pageScope.project.name}</s:param>
                </s:url>
                <s:a href="%{removeProjectUrl}">
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
          </center>
        </ec:column>
      </ec:row>
    </ec:table>
    
  <redback:ifAuthorized permission="continuum-manage-users">
  <h3><s:text name="projectGroup.members.users.title"/></h3>
    
  <s:form action="projectGroupMembers" theme="xhtml" method="post">
    <s:hidden name="ascending" />
    <s:hidden name="projectGroupId" />
    <tr>
      <td nowrap="true">
        <table cellpadding="0" cellspacing="0">               
          <s:select label="%{getText('projectGroup.members.users.search.label')}"
               list="criteria"
               name="filterProperty"
               value="filterProperty" />
        </table>
      </td>               
      <td>
        <table cellpadding="0" cellspacing="0">
          <s:textfield name="filterKey" />
        </table>
      </td>  
      <td colspan="2" align="right">
        <table cellpadding="0" cellspacing="0">
          <s:submit value="%{getText('projectGroup.members.users.search.button')}"/>
        </table>
      </td>
    </tr>             
  </s:form>

  <hr/>
  
  <table class="securityTable" border="1" cellspacing="0" cellpadding="2" width="80%">
    <thead>
      <tr>
        <th nowrap="true">
          <s:form id="sortlist" name="sortlist" action="projectGroupMembers" theme="xhtml" method="post">
            <s:if test="ascending">
              <s:a href="javascript:document.forms['sortlist'].submit()"><img src="<s:url value='/images/icon_sortdown.gif' includeParams="none"/>" title="<s:text name='sort.descending'/>" border="0"></s:a> <s:text name="user.username.label"/>
            </s:if>
            <s:else>
              <s:a href="javascript:document.forms['sortlist'].submit()"><img src="<s:url value='/images/icon_sortup.gif' includeParams="none"/>" title="<s:text name='sort.ascending'/>" border="0"></s:a> <s:text name="user.username.label"/>
            </s:else>
            <s:hidden name="ascending" value="%{!ascending}"/>
            <s:hidden name="projectGroupId" />
            <s:hidden name="sorterProperty" value="username"/>
            <s:hidden name="filterKey" value="%{filterKey}"/>
            <s:hidden name="filterProperty" value="%{filterProperty}"/>
          </s:form>
        </th>   
        <th><s:text name="user.fullName.label"/></th>
        <th><s:text name="user.email.label"/></th>
        <th><s:text name="projectGroup.members.user.role.administrator"/></th>
        <th><s:text name="projectGroup.members.user.role.developer"/></th>
        <th><s:text name="projectGroup.members.user.role.user"/></th>
      </tr>
    </thead>
    <tbody>
      <s:iterator value="projectGroupUsers">
        <tr>
          <td>
            <s:property value="username"/>
          </td>
          <td>
            <s:property value="userFullName"/>
          </td>
          <td>
            <s:property value="userEmail"/>
          </td>
          <td>
            <s:if test="administrator">
              <img src="<s:url value='/images/icon_success_sml.gif' includeParams="none"/>" border="0">
            </s:if>
          </td>
          <td>
            <s:if test="developer">
              <img src="<s:url value='/images/icon_success_sml.gif' includeParams="none"/>" border="0">
            </s:if>
          </td>
          <td>
            <s:if test="user">
              <img src="<s:url value='/images/icon_success_sml.gif' includeParams="none"/>" border="0">
            </s:if>
          </td>
        </tr>
      </s:iterator>
    </tbody>
  </table>
  </redback:ifAuthorized>
  
  </div>
  </body>
</s:i18n>
</html>
