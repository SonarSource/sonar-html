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
    <title>
      <s:text name="projectGroup.page.title"/>
    </title>
    <meta http-equiv="refresh" content="30"/>
    <script type="text/javascript">

      <c:url var="addM2ProjectUrl" value="/addMavenTwoProjectInput!input.action" />
      <c:url var="addM1ProjectUrl" value="/addMavenOneProjectInput!input.action" />
      <c:url var="addProjectUrl" value="/addProjectInput.action" />

      function goToAddProject()
      {
        var projectTypes = document.getElementById( "preferredExecutor" );
        var type = projectTypes.value;

        if ( type != '' )
        {
          var form = document.forms[ "addNewProject" ];

          if ( type == 'maven2' )
          {
            form.action = '${addM2ProjectUrl}';
            form.projectType.value = "";
          }
          else if ( type == 'maven-1' )
          {
            form.action = '${addM1ProjectUrl}';
            form.projectType.value = "";
          }
          else if ( type == 'ant' )
          {
            form.action = '${addProjectUrl}';
            form.projectType.value = "ant";
          }
          else if ( type == 'shell' )
          {
            form.action = '${addProjectUrl}';
            form.projectType.value = "shell";
          }

          form.submit();
        }
        else
        {
          alert( "Please choose a project type to add from the dropdown list." );
        }
      }
    </script>
  </head>
  <body>
  <div id="h3">
    <s:action name="projectGroupTab" executeResult="true">
      <s:param name="tabName" value="'Summary'"/>
    </s:action>

    <h3><s:text name="projectGroup.information.title"><s:param>${projectGroup.name}</s:param></s:text></h3>
    <div class="axial">
      <table border="1" cellspacing="2" cellpadding="3" width="100%">
        <c1:data label="%{getText('projectGroup.name.label')}" name="projectGroup.name"/>
        <c1:data label="%{getText('projectGroup.groupId.label')}" name="projectGroup.groupId"/>
        <c1:data label="%{getText('projectGroup.description.label')}" name="projectGroup.description"/>
        <c1:data label="%{getText('projectGroup.repository.label')}" name="projectGroup.localRepository.name"/>
        <s:if test="url != null">
            <s:url id="projectHomepageUrl" value="%{url}" includeContext="false" includeParams="none"/>
        	<c1:data label="%{getText('projectGroup.url.label')}" name="url" valueLink="%{'${projectHomepageUrl}'}"/>  
        </s:if>
      </table>
    </div>

    <h3><s:text name="projectGroup.scmRoot.title"/></h3>
    <ec:table items="projectScmRoots"
              var="projectScmRoot"
              showExports="false"
              showPagination="false"
              showStatusBar="false"
              sortable="false"
              filterable="false">
      <ec:row highlightRow="true">
        <ec:column property="state" title="&nbsp;" width="1%" cell="org.apache.maven.continuum.web.view.StateCell"/>
        <ec:column property="scmRootAddress" title="projectGroup.scmRoot.label"/>  
      </ec:row>
    </ec:table>

    <redback:ifAnyAuthorized permissions="continuum-build-group,continuum-remove-group" resource="${projectGroup.name}">
      <h3><s:text name="projectGroup.actions.title"/></h3>

      <c:if test="${!empty actionErrors}">
        <div class="errormessage">
          <s:iterator value="actionErrors">
            <p><s:property/></p>
          </s:iterator>
        </div>
      </c:if>

      <div class="functnbar3">
        <table>
          <tr>
            <td>
              <table>
                <redback:ifAuthorized permission="continuum-build-group" resource="${projectGroup.name}">
                  <form action="buildProjectGroup.action" method="post">
                    <input type="hidden" name="projectGroupId" value="<s:property value="projectGroupId"/>"/>
                    <s:select theme="simple" name="buildDefinitionId" list="buildDefinitions"
                               listKey="value" listValue="key" headerKey="-1" headerValue="%{getText('projectGroup.buildDefinition.label')}" />                    
                    <input type="submit" name="build" value="<s:text name="projectGroup.buildGroup"/>"/>
                  </form>
                </redback:ifAuthorized>
              </table>
            </td>
            <td>
              <redback:ifAuthorized permission="continuum-modify-group" resource="${projectGroup.name}">
                <form action="editProjectGroup.action" method="post">
                  <input type="hidden" name="projectGroupId" value="<s:property value="projectGroupId"/>"/>
                  <input type="submit" name="edit" value="<s:text name="edit"/>"/>
                </form>
              </redback:ifAuthorized>
            </td>
            <td>
              <redback:ifAuthorized permission="continuum-build-group" resource="${projectGroup.name}">
                <form action="releaseProjectGroup.action" method="post">
                  <input type="hidden" name="projectGroupId" value="<s:property value="projectGroupId"/>"/>
                  <input type="submit" name="release" value="<s:text name="release"/>"/>
                </form>
              </redback:ifAuthorized>
            </td>
            <td>
              <redback:ifAnyAuthorized permissions="continuum-add-project-to-group" resource="${projectGroup.name}">
                <s:form name="addNewProject">
                  <s:hidden name="disableGroupSelection" value="true"/>
                  <s:hidden name="selectedProjectGroup" value="%{projectGroup.id}"/>
                  <s:hidden name="projectGroupName" value="%{projectGroup.name}"/>
                  <s:hidden name="projectType" value=""/>
                </s:form>

                <s:select theme="simple" name="preferredExecutor" list="#@java.util.HashMap@{'maven2' : 'Add M2 Project', 'maven-1' : 'Add M1 Project', 'ant' : 'Add Ant Project', 'shell' : 'Add Shell Project'}"
                    headerValue="Choose the project type" headerKey="" emptyOption="true" />

                <input type="button" value="<s:text name="add"/>" onclick="goToAddProject()"/>
              </redback:ifAnyAuthorized>
            </td>
            <td>
              <redback:ifAuthorized permission="continuum-remove-group" resource="${projectGroup.name}">
                <form action="removeProjectGroup.action" method="post">
                  <input type="hidden" name="projectGroupId" value="<s:property value="projectGroupId"/>"/>
                  <input type="submit" name="remove" value="<s:text name="projectGroup.deleteGroup"/>"/>
                </form>
              </redback:ifAuthorized>
            </td>
            <td>
              <redback:ifAuthorized permission="continuum-build-project-in-group" resource="${projectGroup.name}">
                <form action="cancelGroupBuild.action" method="post">
                  <input type="hidden" name="projectGroupId" value="<s:property value="projectGroupId"/>"/>
                  <input type="submit" name="cancel" value="<s:text name="projectGroup.cancelGroupBuild"/>"/>
                </form>
              </redback:ifAuthorized>
            </td>
          </tr>
        </table>
      </div>
    </redback:ifAnyAuthorized>

    <s:action name="projectSummary" executeResult="true" namespace="component">
      <s:param name="projectGroupId">${projectGroupId}</s:param>
      <s:param name="projectGroupName">${projectGroup.name}</s:param>
    </s:action>

  </div>
  </body>
</s:i18n>
</html>
