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
<html>
  <s:i18n name="localization.Continuum">
    <head>
      <title><s:text name="buildAgentGroup.page.title"/><title>
    </head>
    <body>
    <div class="app">
      <div id="axial" class="h3">
        <h3><s:text name="buildAgentGroup.section.title"/></h3>

        <div class="axial">
          <s:form action="saveBuildAgentGroup" method="post"  name="buildAgentGroup">
            <c:if test="${!empty actionErrors}">
              <div class="errormessage">
                <s:iterator value="actionErrors">
                  <p><s:property/></p>
                </s:iterator>
              </div>
            </c:if>

            <table>
              <s:hidden name="typeGroup"/>
              <c:choose>
                <c:when test="${typeGroup=='new'}">
                  <s:textfield label="%{getText('buildAgentGroup.name.label')}" name="buildAgentGroup.name" required="true"/>
                </c:when>
                <c:otherwise>
                  <s:hidden name="buildAgentGroup.name"/>
                  <s:textfield label="%{getText('buildAgentGroup.name.label')}" name="buildAgentGroup.name" required="true" disabled="true"/>
                </c:otherwise>
              </c:choose>
            </table>
            
            <c:choose>
              <c:when test="${not empty buildAgents || not empty selectedBuildAgentIds}">
                <table>
                  <s:optiontransferselect
                        label="%{getText('buildAgentGroup.buildAgents.define')}"    
                        name="buildAgentIds"
                        list="buildAgents" 
                        listKey="url"
                        listValue="url"
                        headerKey="-1"
                        headerValue="%{getText('buildAgentGroup.available.buildAgents')}"
                        multiple="true"
                        emptyOption="false"
                        doubleName="selectedBuildAgentIds"
                        doubleList="buildAgentGroup.buildAgents" 
                        doubleListKey="url"
                        doubleListValue="url"
                        doubleHeaderKey="-1"
                        doubleHeaderValue="%{getText('buildAgentGroup.available.buildAgents.used')}" 
                        doubleMultiple="true" 
                        doubleEmptyOption="false"
                        formName="buildAgentGroup"
                        addAllToRightOnclick="selectAllOptionsExceptSome(document.getElementById('saveBuildAgentGroup_selectedBuildAgentIds'), 'key', '-1');"
                        addToRightOnclick="selectAllOptionsExceptSome(document.getElementById('saveBuildAgentGroup_buildAgentIds'), 'key', '-1');selectAllOptionsExceptSome(document.getElementById('saveBuildAgentGroup_selectedBuildAgentIds'), 'key', '-1');"
                        addAllToLeftOnclick="selectAllOptionsExceptSome(document.getElementById('saveBuildAgentGroup_buildAgentIds'), 'key', '-1');"
                        addToLeftOnclick="selectAllOptionsExceptSome(document.getElementById('saveBuildAgentGroup_buildAgentIds'), 'key', '-1');selectAllOptionsExceptSome(document.getElementById('saveBuildAgentGroup_selectedBuildAgentIds'), 'key', '-1');"
                        />
                </table>
              </c:when>
              <c:otherwise>
                <table>
                  <s:text name="buildAgents.empty"/>
                </table>
              </c:otherwise>
            </c:choose>                           

            <div class="functnbar3">
              <c1:submitcancel value="%{getText('save')}" cancel="%{getText('cancel')}"/>
            </div>
          </s:form>
        </div>
      </div>
    </div>
  </s:i18n>
 </html>