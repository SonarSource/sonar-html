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
<s:i18n name="localization.Continuum">
<html>
    <head>
        <title><s:text name="add.m1.project.page.title"/></title>
    </head>
    <body>
        <div class="app">
            <div id="axial" class="h3">
            <h3><s:text name="add.m1.project.section.title"/></h3>
                <div class="axial">
                    <s:form method="post" action="addMavenOneProject.action" name="addMavenOneProject" enctype="multipart/form-data">
                        <c:if test="${!empty actionErrors || !empty errorMessages}">
                          <div class="errormessage">
                            <s:iterator value="actionErrors">
                              <p><s:property/></p>
                            </s:iterator>
                            <c:forEach items="${errorMessages}" var="errorMessage">
                              <p>${errorMessage}</p>
                            </c:forEach>
                          </div>
                        </c:if>
                        <table>
                          <tbody>
                            <s:textfield label="%{getText('add.m1.project.m1PomUrl.label')}" name="m1PomUrl">
                                <s:param name="desc">
                                <table cellspacing="0" cellpadding="0">
                                  <tbody>
                                    <tr>
                                      <td><s:text name="add.m1.project.m1PomUrl.username.label"/>: </td>
                                      <td><input type="text" name="scmUsername" size="20" id="addMavenOneProject_scmUsername"/><td>
                                    </tr>  
                                    <tr>
                                      <td><s:text name="add.m1.project.m1PomUrl.password.label"/>: </td>
                                      <td><input type="password" name="scmPassword" size="20" id="addMavenOneProject_scmPassword"/><td>
                                    </tr>  
                                  </tbody>
                                    <tr>
                                      <td></td>
                                      <td><s:checkbox label="%{getText('projectEdit.project.scmUseCache.label')}" name="scmUseCache"/><td>
                                    </tr>
                                </table>  
                                  <p><s:text name="add.m1.project.m1PomUrl.message"/></p>
                                </s:param>
                            </s:textfield>
                            <s:label>
                              <s:param name="after"><strong><s:text name="or"/></strong></s:param>
                            </s:label>
                            <s:file label="%{getText('add.m1.project.m1PomFile.label')}" name="m1PomFile">
                                <s:param name="desc"><p><s:text name="add.m1.project.m1PomFile.message"/></p></s:param>
                            </s:file>
                            <c:choose>
                            <c:when test="${disableGroupSelection == true}">
                              <s:hidden name="selectedProjectGroup"/>
                              <s:hidden name="disableGroupSelection"/>
                              <s:textfield label="%{getText('add.m1.project.projectGroup')}" name="projectGroupName" disabled="true"/>
                            </c:when>
                            <c:otherwise>
                              <s:select label="%{getText('add.m1.project.projectGroup')}" name="selectedProjectGroup" list="projectGroups" listKey="id" listValue="name"/>
                            </c:otherwise>
                            </c:choose>
                            <s:select label="%{getText('add.m1.project.buildDefinitionTemplate')}" name="buildDefinitionTemplateId"
                                       list="buildDefinitionTemplates" listKey="id" listValue="name" headerKey="-1" 
                                       headerValue="%{getText('add.m1.project.defaultBuildDefinition')}"/>                            
                          </tbody>
                        </table>
                        <div class="functnbar3">
                          <c1:submitcancel value="%{getText('add')}" cancel="%{getText('cancel')}"/>
                        </div>
                  </s:form>
                </div>
            </div>
        </div>
    </body>
</html>
</s:i18n>
