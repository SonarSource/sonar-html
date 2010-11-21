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
<%@ taglib prefix="c1" uri="continuum" %>
<%@ taglib uri="http://plexus.codehaus.org/redback/taglib-1.0" prefix="redback" %>
<html>
  <s:i18n name="localization.Continuum">
    <head>
        <title><s:text name="buildResult.page.title"/></title>
    </head>
    <body>
      <div id="h3">

        <jsp:include page="/WEB-INF/jsp/navigations/ProjectMenu.jsp"/>

        <h3>
            <s:text name="buildResult.section.title">
                <s:param><s:property value="project.name"/></s:param>
            </s:text>
        </h3>

        <div class="axial">
          <table border="1" cellspacing="2" cellpadding="3" width="100%">
            <c1:data label="%{getText('buildResult.startTime')}">
                <s:param name="after"><c1:date name="buildResult.startTime"/></s:param>
            </c1:data>
            <c1:data label="%{getText('buildResult.endTime')}">
                <s:param name="after"><c1:date name="buildResult.endTime"/></s:param>
            </c1:data>
            <c1:data label="%{getText('buildResult.duration')}">
                <s:param name="after">
                    <s:if test="buildResult.endTime == 0"><s:text name="buildResult.startedSince"/></s:if> <s:property value="buildResult.durationTime"/></s:param>
            </c1:data>
            <c1:data label="%{getText('buildResult.trigger')}">
                <s:param name="after"><s:text name="buildResult.trigger.%{buildResult.trigger}"/></s:param>
            </c1:data>
            <c1:data label="%{getText('buildResult.state')}">
                <s:param name="after" value="state"/>
            </c1:data>
            <c1:data label="%{getText('buildResult.buildNumber')}">
                <s:param name="after">
                    <s:if test="buildResult.buildNumber != 0">
                        <s:property value="buildResult.buildNumber"/>
                    </s:if>
                    <s:else>
                        &nbsp;
                    </s:else>
                </s:param>
            </c1:data>
            <c1:data label="%{getText('buildResult.username')}">
                <s:param name="after"><s:property value="buildResult.username"/></s:param>
            </c1:data>
          </table>
        </div>
        <div class="functnbar3">
          <table>
            <tbody>
            <tr>
              <td>
                <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroupName}">
                  <form action="removeBuildResult.action">
                    <input type="hidden" name="projectId" value="<s:property value="projectId"/>"/>
                    <input type="hidden" name="buildId" value="<s:property value="buildId"/>"/>
                    <s:if test="canDelete">
                      <input type="submit" name="delete-project" value="<s:text name="delete"/>"/>
                    </s:if>
                    <s:else>
                      <input type="submit" disabled="true" name="delete-project" value="<s:text name="delete"/>"/>
                    </s:else>
                  </form>
                </redback:ifAuthorized>
              </td>
            </tr>
            </tbody>
          </table>
        </div>

        <h4><s:text name="buildResult.scmResult.changes"/></h4>
        <s:if test="buildResult.scmResult.changes != null && buildResult.scmResult.changes.size() > 0">
            <s:set name="changes" value="buildResult.scmResult.changes" scope="request"/>
            <ec:table items="changes"
                      var="change"
                      showExports="false"
                      showPagination="false"
                      showStatusBar="false"
                      sortable="false"
                      filterable="false">
              <ec:row>
                <ec:column property="author" title="buildResult.scmResult.changes.author"/>
                <ec:column property="date" title="buildResult.scmResult.changes.date" cell="date"/>
                <ec:column property="comment" title="buildResult.scmResult.changes.comment" />
                <ec:column property="files" title="buildResult.scmResult.changes.files">
                    <c:forEach var="scmFile" items="${pageScope.change.files}">
                        <c:out value="${scmFile.name}"/><br />
                    </c:forEach>
                </ec:column>
              </ec:row>
            </ec:table>
        </s:if>
        <s:else>
          <b><s:text name="buildResult.scmResult.noChanges"/></b>
        </s:else>

        <s:if test="changesSinceLastSuccess != null && changesSinceLastSuccess.size() > 0">
            <h4><s:text name="buildResult.changesSinceLastSuccess"/></h4>
            <s:set name="changes" value="changesSinceLastSuccess" scope="request"/>
            <ec:table items="changes"
                      var="change"
                      showExports="false"
                      showPagination="false"
                      showStatusBar="false"
                      sortable="false"
                      filterable="false">
              <ec:row>
                <ec:column property="author" title="buildResult.changes.author"/>
                <ec:column property="date" title="buildResult.changes.date" cell="date"/>
                <ec:column property="comment" title="buildResult.changes.comment" />
                <ec:column property="files" title="buildResult.changes.files">
                    <c:forEach var="scmFile" items="${pageScope.change.files}">
                        <c:out value="${scmFile.name}"/><br />
                    </c:forEach>
                </ec:column>
              </ec:row>
            </ec:table>
        </s:if>

        <h4><s:text name="buildResult.dependencies.changes"/></h4>
        <s:if test="buildResult.modifiedDependencies != null && buildResult.modifiedDependencies.size() > 0">
            <s:set name="dependencies" value="buildResult.modifiedDependencies" scope="request"/>
            <ec:table items="dependencies"
                      var="dep"
                      showExports="false"
                      showPagination="false"
                      showStatusBar="false"
                      sortable="false"
                      filterable="false">
              <ec:row>
                <ec:column property="groupId" title="buildResult.dependencies.groupId"/>
                <ec:column property="artifactId" title="buildResult.dependencies.artifactId"/>
                <ec:column property="version" title="buildResult.dependencies.version"/>
              </ec:row>
            </ec:table>
        </s:if>
        <s:else>
          <b><s:text name="buildResult.dependencies.noChanges"/></b>
        </s:else>
        
        <h4><s:text name="buildResult.buildDefinition"/></h4>
          <table border="1" cellspacing="2" cellpadding="3" width="80%">
            <tbody>
              <s:if test="buildResult.buildDefinition.type='ant'">
                <tr class="b">
                  <th><s:text name="buildResult.buildDefinition.ant.label"/></th>
                  <td><s:property value="buildResult.buildDefinition.buildFile"/></td>
                </tr>               
              </s:if>
              <s:elseif test="buildResult.buildDefinition.type='shell'">
                <tr class="b">
                  <th><s:text name="buildResult.buildDefinition.shell.label"/></th>
                  <td><s:property value="buildResult.buildDefinition.buildFile"/></td>
                </tr>               
              </s:elseif>
              <s:else>
                <tr class="b">
                  <th><s:text name="buildResult.buildDefinition.maven.label"/></th>
                  <td><s:property value="buildResult.buildDefinition.buildFile"/></td>
                </tr>               
              </s:else>
              <tr class="b">
                <th><s:text name="buildResult.buildDefinition.goals"/></th>
                <td><s:property value="buildResult.buildDefinition.goals"/></td>
              </tr>
              <tr class="b">
                <th><s:text name="buildResult.buildDefinition.arguments"/></th>
                <td><s:property value="buildResult.buildDefinition.arguments"/></td>
              </tr>
              <tr class="b">
                <th><s:text name="buildResult.buildDefinition.buildFresh"/></th>
                <td><s:property value="buildResult.buildDefinition.buildFresh"/></td>
              </tr>
              <tr class="b">
                <th><s:text name="buildResult.buildDefinition.alwaysBuild"/></th>
                <td><s:property value="buildResult.buildDefinition.alwaysBuild"/></td>
              </tr>
              <tr class="b">
                <th><s:text name="buildResult.buildDefinition.defaultForProject"/></th>
                <td><s:property value="buildResult.buildDefinition.defaultForProject"/></td>
              </tr>
              <tr class="b">
                <th><s:text name="buildResult.buildDefinition.schedule"/></th>
                <td><s:property value="buildResult.buildDefinition.schedule.name"/></td>
              </tr>
              <s:if test="buildResult.buildDefinition.profile != null">
                <tr class="b">
                  <th><s:text name="buildResult.buildDefinition.profileName"/></th>
                  <td><s:property value="buildResult.buildDefinition.profile.name"/></td>
                </tr>          
              </s:if>
              <tr class="b">
                <th><s:text name="buildResult.buildDefinition.description"/></th>
                <td><s:property value="buildResult.buildDefinition.description"/></td>
              </tr>              
            </tbody>
          </table> 

        <s:if test="hasSurefireResults">
          <h4><s:text name="buildResult.generatedReports.title"/></h4>

          <s:url id="surefireReportUrl" action="surefireReport">
            <s:param name="projectId" value="projectId"/>
            <s:param name="buildId" value="buildId"/>
            <s:param name="projectName" value="projectName"/>
          </s:url>
          <s:a href="%{surefireReportUrl}"><s:text name="buildResult.generatedReports.surefire"/></s:a>
        </s:if>

        <s:if test="buildResult.state == 4">
          <h4><s:text name="buildResult.buildError"/></h4>
          <div style="width:100%; height:500px; overflow:auto; border-style: solid; border-width: 1px">
            <pre><s:property value="buildResult.error"/></pre>
          </div>
        </s:if>
        <s:else>
          <h4><s:text name="buildResult.buildOutput"/></h4>
          <p>
            <s:if test="buildOutput == ''">
                <s:text name="buildResult.noOutput"/>
            </s:if>
            <s:else>
              <s:url id="buildOutputTextUrl" action="buildOutputText">
                <s:param name="projectId" value="projectId"/>
                <s:param name="buildId" value="buildId"/>
              </s:url>
              <s:a href="%{buildOutputTextUrl}"><s:text name="buildResult.buildOutput.text"/></s:a>
              <div style="width:100%; height:500px; overflow:auto; border-style: solid; border-width: 1px">
                <pre><s:property value="buildOutput"/></pre>
              </div>
            </s:else>
          </p>
        </s:else>
      </div>
    </body>
  </s:i18n>
</html>
