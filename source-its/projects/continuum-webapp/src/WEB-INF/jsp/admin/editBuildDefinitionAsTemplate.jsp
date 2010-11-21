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
        <title><s:text name="buildDefinition.template.page.title"/></title>
    </head>
    <body>
      <div id="axial" class="h3">
        <h3><s:text name="buildDefinition.template.section.title"/></h3>

        <div class="axial">
          <s:form action="saveBuildDefinitionAsTemplate" method="post" validate="true">
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
                    <s:if test="buildDefinition.type == 'ant'">
                      <s:textfield label="%{getText('buildDefinition.buildFile.ant.label')}" name="buildDefinition.buildFile"  required="true"/>
                    </s:if>
                    <s:elseif test="buildDefinition.type == 'shell'">
                      <s:textfield label="%{getText('buildDefinition.buildFile.shell.label')}" name="buildDefinition.buildFile" required="true"/>
                    </s:elseif>
                    <s:else>
                      <s:textfield label="%{getText('buildDefinition.buildFile.maven.label')}" name="buildDefinition.buildFile" required="true"/>
                    </s:else>
    
                    <s:if test="buildDefinition.type == 'ant'">
                      <s:textfield label="%{getText('buildDefinition.goals.ant.label')}" name="buildDefinition.goals"/>
                    </s:if>
                    <s:elseif test="buildDefinition.type == 'shell'">
                    </s:elseif>
                    <s:else>
                      <s:textfield label="%{getText('buildDefinition.goals.maven.label')}" name="buildDefinition.goals"/>
                    </s:else>
    
                    <s:textfield label="%{getText('buildDefinition.arguments.label')}" name="buildDefinition.arguments"/>
                    <s:checkbox label="%{getText('buildDefinition.buildFresh.label')}" name="buildDefinition.buildFresh"/>
                    <s:checkbox label="%{getText('buildDefinition.alwaysBuild.label')}" name="buildDefinition.alwaysBuild" />
                    <s:checkbox label="%{getText('buildDefinition.defaultForProject.label')}" name="buildDefinition.defaultForProject" />
                    <s:select label="%{getText('buildDefinition.schedule.label')}" name="buildDefinition.schedule.id" list="schedules" listValue="name"
                               listKey="id"/>
                    <s:if test="buildDefinition.profile == null">
                      <s:select label="%{getText('buildDefinition.profile.label')}" name="buildDefinition.profile.id" list="profiles" listValue="name"
                                 value="-1" listKey="id" headerKey="-1" headerValue=""/>
                    </s:if>
                    <s:else>
                      <s:select label="%{getText('buildDefinition.profile.label')}" name="buildDefinition.profile.id" list="profiles" listValue="name"
                                 listKey="id" headerKey="-1" headerValue=""/>
                    </s:else>
                    <s:select label="%{getText('buildDefinition.type.label')}" name="buildDefinition.type" list="buildDefinitionTypes"/>
                    <s:if test="buildDefinition.type != 'ant' || buildDefinition.type != 'shell'">
                        <s:select label="%{getText('buildDefinition.updatePolicy.label')}" name="buildDefinition.updatePolicy" list="buildDefinitionUpdatePolicies"/>
                    </s:if>
                    <s:textfield label="%{getText('buildDefinition.description.label')}" name="buildDefinition.description" required="true"/>
                  </tbody>
                </table>
                <div class="functnbar3">
                  <c1:submitcancel value="%{getText('save')}" cancel="%{getText('cancel')}"/>
                </div>

                <s:hidden name="buildDefinition.id"/>
                <s:hidden name="buildDefinition.template" value="true"/>
              </c:when>
            
            </c:choose>
          </s:form>
        </div>
      </div>
    </body>
  </s:i18n>
</html>
