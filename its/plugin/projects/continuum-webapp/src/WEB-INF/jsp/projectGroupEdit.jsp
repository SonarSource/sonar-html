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
<html>
  <s:i18n name="localization.Continuum">
    <head>
        <title><s:text name="projectGroup.edit.page.title"/></title>
    </head>
    <body>
      <div id="axial" class="h3">
        <h3><s:text name="projectGroup.edit.section.title"/></h3>

        <div class="axial">
          <s:form action="saveProjectGroup" method="post" validate="true">
              <s:if test="projectInCOQueue">
                <div class="label">
                    <p><s:text name="%{getText('project.in.checkout.queue.error')}"/></p>
                            </div >
              </s:if>
              <c:if test="${!empty actionErrors}">
              <div class="errormessage">
                <s:iterator value="actionErrors">
                  <p><s:property/></p>
                </s:iterator>
              </div>
              </c:if>
            <table>
              <tbody>
                <s:hidden name="projectGroupId"/>
                <s:textfield label="%{getText('projectGroup.name.label')}" name="name" required="true" disabled="%{projectInCOQueue}"/>
                <c1:data label="%{getText('projectGroup.groupId.label')}" name="projectGroup.groupId"/>
                <s:textfield label="%{getText('projectGroup.description.label')}" name="description" disabled="%{projectInCOQueue}"/>
                <s:select label="%{getText('projectGroup.repository.label')}" name="repositoryId" list="repositories"
                           listKey="id" listValue="name" disabled="%{disabledRepositories}"/> 
                <s:textfield label="%{getText('projectGroup.url.label')}" name="url" disabled="%{projectInCOQueue}"/>
              </tbody>
            </table>
            
            <c:if test="${!empty projectList}">
            <h3><s:text name="projectGroup.edit.section.projects.title"/></h3>
            <div class="eXtremeTable">
              <table id="projects_table" border="1" cellspacing="2" cellpadding="3" class="tableRegion" width="100%">
                <thead>
                  <tr>
                    <td class="tableHeader"><s:text name="projectGroup.edit.project.name"/></td>
                    <td class="tableHeader"><s:text name="projectGroup.edit.move.to.group"/></td>
                  </tr>
                </thead>
                <tbody class="tableBody">
                  <s:iterator value="projectList" status="rowCounter">
                    <tr class="<s:if test="#rowCounter.odd == true">odd</s:if><s:else>even</s:else>">
                      <td><s:select cssStyle="width:200px" label="%{name}" name="projects[%{id}]" list="projectGroups" value="%{projectGroup.id}" disabled="%{projectInCOQueue}"/></td>
                    </tr>
                  </s:iterator>
                </tbody>
              </table>
            </div>
            </c:if>
            <div class="functnbar3">
              <c:choose>
                <c:when test="${!projectInCOQueue}">
                  <c1:submitcancel value="%{getText('save')}" cancel="%{getText('cancel')}"/>
                </c:when>
                <c:otherwise>
                  <input type="button" value="<s:text name="back"/>" onClick="history.go(-1)">
                </c:otherwise>
              </c:choose>
            </div>
          </s:form>
        </div>
      </div>
    </body>
  </s:i18n>
</html>
