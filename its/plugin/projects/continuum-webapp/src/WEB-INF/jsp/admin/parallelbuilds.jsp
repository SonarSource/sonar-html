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
    <title><s:text name="parallel.build.queues.page.title"/></title>
  </head>  
  <body>
    <div id="h3">  
      <h3>
        <s:text name="parallel.build.queues.section.title"/>
      </h3>
      
      <c:if test="${not empty buildQueueList}">
      <ec:table items="buildQueueList"
              var="buildQueue"
              showExports="false"
              showPagination="false"
              showStatusBar="false"
              sortable="false"
              filterable="false">
        <ec:row highlightRow="true">
        <ec:column property="name" title="Name" style="white-space: nowrap" />
           
        <ec:column property="id" title="&nbsp;" width="1%">
          <c:if test="${buildQueue.id != 1}"> 
            <s:url id="deleteBuildQueueUrl" action="deleteBuildQueue">
              <s:param name="buildQueue.id">${pageScope.buildQueue.id}</s:param>
              <s:param name="buildQueue.name">${pageScope.buildQueue.name}</s:param>
            </s:url>          
            <s:a href="%{deleteBuildQueueUrl}">
              <img src="<s:url value='/images/delete.gif' includeParams="none"/>" alt="<s:text name='delete'/>" title="<s:text name='delete'/>" border="0" />
            </s:a>
          </c:if>
          <c:if test="${buildQueue.id == 1}">
              <img src="<s:url value='/images/delete_disabled.gif' includeParams="none"/>" alt="<s:text name='delete'/>" title="<s:text name='delete'/>" border="0" />
          </c:if>
        </ec:column>             
        </ec:row>
        </ec:table>
      </c:if>
      
       <div class="functnbar3">
        <s:form action="addBuildQueue!input.action" method="post">
          <s:submit value="%{getText('add')}"/>
        </s:form>
      </div> 
    </div>
  </body>
  </s:i18n>
</html>