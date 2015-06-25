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
        <title><s:text name="repositories.page.title"/></title>
    </head>
    <body>
      <div id="h3">
        <h3><s:text name="repositories.section.title"/></h3>
        <c:if test="${!empty actionErrors}">
          <div class="errormessage">
            <s:iterator value="actionErrors">
              <p><s:property/></p>
            </s:iterator>
          </div>
        </c:if>
        <s:set name="repositories" value="repositories" scope="request"/>
        <ec:table items="repositories"
                  var="repository"
                  showExports="false"
                  showPagination="false"
                  showStatusBar="false"
                  sortable="false"
                  filterable="false">
         <ec:row>
            <ec:column property="name" title="repositories.table.name"/>
            <ec:column property="location" title="repositories.table.location"/>
            <ec:column property="layout" title="repositories.table.layout"/>
            <ec:column property="editActions" title="&nbsp;" width="1%">
                <s:url id="editRepositoryUrl" action="editRepository">
                  <s:param name="repository.id">${pageScope.repository.id}</s:param>
                </s:url>
                <c:choose>
                  <c:when test="${repository.name == 'DEFAULT'}">
                    <img src="<s:url value='/images/edit_disabled.gif' includeParams="none"/>" alt="<s:text name='edit'/>" title="<s:text name='edit'/>" border="0" />
                  </c:when>
                  <c:otherwise>
                    <s:a href="%{editRepositoryUrl}"><img src="<s:url value='/images/edit.gif' includeParams="none"/>" alt="<s:text name='edit'/>" title="<s:text name='edit'/>" border="0" /></s:a>
                  </c:otherwise>
                </c:choose>
            </ec:column>
            <ec:column property="purgeActions" title="&nbsp;" width="1%">
              <c:set var="repositoryName" value="${pageScope.repository.name}" scope="request"/>
              <c:choose>
                <c:when test="${defaultPurgeMap[repositoryName]}">
                  <s:url id="purgeRepositoryUrl" action="purgeRepository">
                    <s:param name="repository.id">${pageScope.repository.id}</s:param>
                  </s:url>
                  <s:a href="%{purgeRepositoryUrl}"><img src="<s:url value='/images/purgenow.gif' includeParams="none"/>" alt="<s:text name='purge'/>" title="<s:text name='purge'/>" border="0" /></s:a>
                </c:when>
                <c:otherwise>
                  <s:a href="%{purgeRepositoryUrl}"><img src="<s:url value='/images/disabled_purgenow.gif' includeParams="none"/>" alt="<s:text name='purge'/>" title="<s:text name='purge'/>" border="0" /></s:a>
                </c:otherwise>
              </c:choose>
            </ec:column>
            <ec:column property="deleteActions" title="&nbsp;" width="1%">
                <s:url id="removeRepositoryUrl" action="removeRepository">
                  <s:param name="repository.id">${pageScope.repository.id}</s:param>
                </s:url>
                <c:choose>
                  <c:when test="${repository.name == 'DEFAULT'}">
                    <img src="<s:url value='/images/delete_disabled.gif' includeParams="none"/>" alt="<s:text name='delete'/>" title="<s:text name='delete'/>" border="0">
                  </c:when>
                  <c:otherwise>
                    <s:a href="%{removeRepositoryUrl}"><img src="<s:url value='/images/delete.gif' includeParams="none"/>" alt="<s:text name='delete'/>" title="<s:text name='delete'/>" border="0"></s:a>
                  </c:otherwise>
                </c:choose>
            </ec:column>
          </ec:row>
        </ec:table>
      </div>
      <div class="functnbar3">
        <s:form action="editRepository" method="post">
          <s:submit value="%{getText('add')}"/>
        </s:form>
      </div>
    </body>
  </s:i18n>
</html>
