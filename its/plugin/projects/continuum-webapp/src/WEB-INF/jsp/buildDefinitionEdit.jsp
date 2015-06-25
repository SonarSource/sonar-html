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
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="continuum" prefix="c1" %>
<html>
  <s:i18n name="localization.Continuum">
    <head>
        <title><s:text name="buildDefinition.page.title"/></title>
        <script language="javascript">
          function setAlwaysBuild()
          {
              if ( document.saveBuildDefinition.buildFresh.checked == true )
              {
                  document.saveBuildDefinition.alwaysBuild.checked = true;
                  document.saveBuildDefinition.alwaysBuild.disabled = true;
              }
              else
              {
                  document.saveBuildDefinition.alwaysBuild.checked = false;
                  document.saveBuildDefinition.alwaysBuild.disabled = false;
              }
          }
        </script>
    </head>
    <body>
      <div id="axial" class="h3">
        <h3><s:text name="buildDefinition.section.title"/></h3>

        <div class="axial">
          <s:form action="saveBuildDefinition" method="post">
            <c:choose>
            
              <c:when test="${!empty actionErrors}">
                <div class="errormessage">
                  <s:iterator value="actionErrors">
                    <p><s:property/></p>
                  </s:iterator>
                </div>
                <input type="button" value="Back" onClick="history.go(-1)">
              </c:when>
  
              <c:when test="${empty actionErrors}">
                <table>
                  <tbody>
                    <s:if test="executor == 'ant'">
                      <s:textfield label="%{getText('buildDefinition.buildFile.ant.label')}" name="buildFile"  required="true"/>
                    </s:if>
                    <s:elseif test="executor == 'shell'">
                      <s:textfield label="%{getText('buildDefinition.buildFile.shell.label')}" name="buildFile" required="true"/>
                    </s:elseif>
                    <s:else>
                      <s:textfield label="%{getText('buildDefinition.buildFile.maven.label')}" name="buildFile" required="true"/>
                    </s:else>
    
                    <s:if test="executor == 'ant'">
                      <s:textfield label="%{getText('buildDefinition.goals.ant.label')}" name="goals"/>
                    </s:if>
                    <s:elseif test="executor == 'shell'">
                    </s:elseif>
                    <s:else>
                      <s:textfield label="%{getText('buildDefinition.goals.maven.label')}" name="goals"/>
                    </s:else>
    
                    <s:textfield label="%{getText('buildDefinition.arguments.label')}" name="arguments"/>
                    <s:checkbox label="%{getText('buildDefinition.buildFresh.label')}" id="buildFresh" name="buildFresh" value="buildFresh" fieldValue="true" onclick="setAlwaysBuild();"/>
                    <c:choose>
                    <c:when test="${buildFresh == true}">
                      <s:checkbox label="%{getText('buildDefinition.alwaysBuild.label')}" id="alwaysBuild" name="alwaysBuild" value="true" disabled="true"/>
                    </c:when>
                    <c:otherwise>
                      <s:checkbox label="%{getText('buildDefinition.alwaysBuild.label')}" id="alwaysBuild" name="alwaysBuild"/>
                    </c:otherwise>
                    </c:choose>
                    <c:choose>
                    <c:when test="${defaultBuildDefinition == true}">
                      <s:label label="%{getText('buildDefinition.defaultForProject.label')}" value="true"/>
                    </c:when>
                    <c:otherwise>
                      <s:checkbox label="%{getText('buildDefinition.defaultForProject.label')}" name="defaultBuildDefinition" value="defaultBuildDefinition" fieldValue="true"/>
                    </c:otherwise>
                    </c:choose>
                    <s:select label="%{getText('buildDefinition.schedule.label')}" name="scheduleId" list="schedules"/>
                    <s:select label="%{getText('buildDefinition.profile.label')}" name="profileId" list="profiles" listValue="name"
                               listKey="id" headerKey="-1" headerValue=""/>
                    <s:select label="%{getText('buildDefinition.type.label')}" name="buildDefinitionType" list="buildDefinitionTypes"/>
                    <s:if test="executor != 'ant' || executor != 'shell'">
                        <s:select label="%{getText('buildDefinition.updatePolicy.label')}" name="updatePolicy" list="buildDefinitionUpdatePolicies"/>
                    </s:if>
                    <s:textfield label="%{getText('buildDefinition.description.label')}" name="description" />
                  </tbody>
                </table>
                <div class="functnbar3">
                  <c1:submitcancel value="%{getText('save')}" cancel="%{getText('cancel')}"/>
                </div>

                <s:hidden name="buildDefinitionId"/>
                <s:hidden name="projectId"/>
                <s:hidden name="projectGroupId"/>
                <s:hidden name="groupBuildDefinition"/>
                <c:if test="${groupBuildView == true}">
                  <s:hidden name="groupBuildView" value="true"/>
                </c:if>
                <c:choose>
                <c:when test="${defaultBuildDefinition == true}">
                  <s:hidden name="defaultBuildDefinition" value="true"/>
                </c:when>
                </c:choose>
              </c:when>
            
            </c:choose>
          </s:form>
        </div>
      </div>
    </body>
  </s:i18n>
</html>
