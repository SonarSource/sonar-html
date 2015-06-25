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
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ taglib uri="continuum" prefix="c1" %>
<%@ taglib uri="http://plexus.codehaus.org/redback/taglib-1.0" prefix="redback" %>

<s:i18n name="localization.Continuum">
<div id="navcolumn">
  <div id="projectmenu" class="toolgroup">
    <div class="label"><s:text name="menu.continuum"/></div>
    <div>
      <div class="body">
        <s:url id="aboutUrl" action="about" namespace="/" includeParams="none"/>
        <s:a href="%{aboutUrl}">
          <s:text name="menu.continuum.about"/>
        </s:a>
      </div>
      <div class="body">
        <s:url id="groupSummaryUrl" action="groupSummary" namespace="/" includeParams="none"/>
        <s:a href="%{groupSummaryUrl}">
          <s:text name="menu.continuum.showProjectGroups"/>
        </s:a>
      </div>
    </div>
  </div>

  <redback:ifAuthorized permission="continuum-add-group">
    <div id="projectmenu" class="toolgroup">
      <div class="label">
        <s:text name="menu.addProject"/>
      </div>
      <div>
        <div class="body">
          <s:url id="addMavenTwoProjectUrl" action="addMavenTwoProjectInput" method="input" namespace="/"
                  includeParams="none"/>
          <s:a href="%{addMavenTwoProjectUrl}">
            <s:text name="menu.add.m2Project"/>
          </s:a>
        </div>
        <div class="body">
          <s:url id="addMavenOneProjectUrl" action="addMavenOneProjectInput" method="input" namespace="/"
                  includeParams="none"/>
          <s:a href="%{addMavenOneProjectUrl}">
            <s:text name="menu.add.m1Project"/>
          </s:a>
        </div>
        <div class="body">
          <s:url id="addAntProjectUrl" action="addProjectInput" namespace="/" includeParams="none">
            <s:param name="projectType">ant</s:param>
          </s:url>
          <s:a href="%{addAntProjectUrl}">
            <s:text name="menu.add.antProject"/>
          </s:a>
        </div>
        <div class="body">
          <s:url id="addShellProjectUrl" action="addProjectInput" namespace="/" includeParams="none">
            <s:param name="projectType">shell</s:param>
          </s:url>
          <s:a href="%{addShellProjectUrl}">
            <s:text name="menu.add.shellProject"/>
          </s:a>
        </div>
      </div>
    </div>
  </redback:ifAuthorized>


  <redback:ifAnyAuthorized permissions="continuum-manage-build-templates,continuum-manage-schedules,continuum-manage-configuration,continuum-manage-users,continuum-manage-installations,continuum-manage-profiles,continuum-view-queues,continuum-manage-repositories,continuum-manage-purging">
    <div id="projectmenu" class="toolgroup">
      <div class="label">
        <s:text name="menu.administration"/>
      </div>
      <div>
        <redback:ifAuthorized permission="continuum-manage-repositories">
          <s:url id="repositoryListUrl" action="repositoryList" namespace="/admin" includeParams="none"/>
          <div class="body">
            <s:a href="%{repositoryListUrl}">
              <s:text name="menu.administration.repositories"/>
            </s:a>
          </div>
        </redback:ifAuthorized>
        <redback:ifAuthorized permission="continuum-manage-purging">
          <s:url id="purgeConfigListUrl" action="purgeConfigList" namespace="/admin" includeParams="none"/>
          <div class="body">
            <s:a href="%{purgeConfigListUrl}">
              <s:text name="menu.administration.purge"/>
            </s:a>
          </div>
        </redback:ifAuthorized>
        <redback:ifAuthorized permission="continuum-manage-schedules">
          <s:url id="scheduleUrl" namespace="/" action="schedules" includeParams="none"/>
          <div class="body">
            <s:a href="%{scheduleUrl}">
              <s:text name="menu.administration.schedules"/>
            </s:a>
          </div>
        </redback:ifAuthorized>
        <redback:ifAuthorized permission="continuum-manage-installations">
          <s:url id="configurationUrl" action="installationsList" namespace="/admin" method="list" includeParams="none"/>
          <div class="body">
            <s:a href="%{configurationUrl}">
              <s:text name="menu.administration.installations"/>
            </s:a>
          </div>
        </redback:ifAuthorized>
        <redback:ifAuthorized permission="continuum-manage-profiles">
          <s:url id="configurationUrl" action="buildEnvList" namespace="/admin" method="list" includeParams="none"/>
          <div class="body">
            <s:a href="%{configurationUrl}">
              <s:text name="menu.administration.profile"/>
            </s:a>
          </div> 
        </redback:ifAuthorized> 
        <redback:ifAuthorized permission="continuum-view-queues">
          <s:url id="queueUrls" action="displayQueues" namespace="/admin" method="display" includeParams="none"/>
          <div class="body">
            <s:a href="%{queueUrls}">
              <s:text name="menu.administration.queues"/>
            </s:a>
          </div> 
        </redback:ifAuthorized>
        <redback:ifAuthorized permission="continuum-manage-build-templates">
          <s:url id="buildDefinitionTemplatesUrl" action="buildDefinitionTemplates" namespace="/admin" includeParams="none"/>
          <div class="body">
            <s:a href="%{buildDefinitionTemplatesUrl}">
              <s:text name="menu.administration.buildDefinitionTemplates"/>
            </s:a>
          </div> 
        </redback:ifAuthorized>        
        <redback:ifAuthorized permission="continuum-manage-configuration">               
          <s:url id="configurationUrl" action="configuration" namespace="/admin" method="input" includeParams="none"/>
          <div class="body">
            <s:a href="%{configurationUrl}">
              <s:text name="menu.administration.configuration"/>
            </s:a>
          </div>
          <s:url id="configurationUrl" action="configureAppearance" namespace="/admin" includeParams="none"/>
          <div class="body">
            <s:a href="%{configurationUrl}">
              <s:text name="menu.administration.appearance"/>
            </s:a>
          </div>
        </redback:ifAuthorized>
        <redback:ifAuthorized permission="continuum-manage-users">
          <s:url id="userListUrl" action="userlist" namespace="/security" includeParams="none"/>
          <div class="body">
            <s:a href="%{userListUrl}">
              <s:text name="menu.administration.users"/>
            </s:a>
          </div>
          <s:url id="roleListUrl" action="roles" namespace="/security" includeParams="none"/>
          <div class="body">
            <s:a href="%{roleListUrl}">
              <s:text name="menu.administration.roles"/>
            </s:a>
          </div>
        </redback:ifAuthorized>
      </div>
    </div>
  </redback:ifAnyAuthorized>

  <redback:ifAuthorized permission="continuum-view-report">
    <div id="projectmenu" class="toolgroup">
      <div class="label">
        <s:text name="menu.reports"/>
      </div>
      <div>
        <div class="body">
          <s:url id="viewProjectBuildsReportUrl" action="viewProjectBuildsReport" method="init" namespace="/" includeParams="none"/>
          <s:a href="%{viewProjectBuildsReportUrl}">
            <s:text name="menu.reports.projectBuilds"/>
          </s:a> 
        </div>
      </div>
    </div>
  </redback:ifAuthorized>

  <c1:ifBuildTypeEnabled buildType="distributed">
    <redback:ifAnyAuthorized permissions="continuum-manage-distributed-builds,continuum-view-release">
      <div id="projectmenu" class="toolgroup">
        <div class="label">
          <s:text name="menu.distributedBuilds"/>
        </div>    
        <div>
          <redback:ifAuthorized permission="continuum-manage-distributed-builds">    
            <s:url id="buildAgentList" action="buildAgentList" namespace="/security" includeParams="none" />
            <div class="body">
              <s:a href="%{buildAgentList}">
                <s:text name="menu.distributedBuilds.buildAgents"/>
              </s:a>
            </div>
          </redback:ifAuthorized>
          <redback:ifAuthorized permission="continuum-view-release">
            <s:url id="releasesUrl" action="viewReleases" namespace="/" includeParams="none"/>
            <div class="body">
              <s:a href="%{releasesUrl}">
                <s:text name="menu.distributedBuilds.releases"/>
              </s:a>
            </div>
          </redback:ifAuthorized>            
        </div>
      </div>
    </redback:ifAnyAuthorized>
  </c1:ifBuildTypeEnabled>
  
  <c1:ifBuildTypeEnabled buildType="parallel">
    <redback:ifAuthorized permission="continuum-manage-parallel-builds">
      <div id="projectmenu" class="toolgroup">
        <div class="label">
          <s:text name="menu.parallelBuilds"/>
        </div>    
        <div>
          <s:url id="buildQueueListUrl" action="buildQueueList" namespace="/admin" includeParams="none"/>
          <div class="body">
            <s:a href="%{buildQueueListUrl}">
              <s:text name="menu.parallelBuilds.build.queue"/>
            </s:a>
          </div>          
        </div>
      </div>
    </redback:ifAuthorized>
  </c1:ifBuildTypeEnabled>
  
  <div id="projectmenu" class="toolgroup">
    <div class="label"><s:text name="legend.title"/></div>
    <div id="legend">
      <div id="litem1" class="body"><s:text name="legend.buildNow"/></div>
      <div id="litem2" class="body"><s:text name="legend.buildHistory"/></div>
      <div id="litem3" class="body"><s:text name="legend.buildInProgress"/></div>
      <div id="litem4" class="body"><s:text name="legend.workingCopy"/></div>
      <div id="litem5" class="body"><s:text name="legend.checkingOutBuild"/></div>
      <div id="litem6" class="body"><s:text name="legend.queuedBuild"/></div>
      <div id="litem7" class="body"><s:text name="legend.cancelBuild"/></div>
      <div id="litem8" class="body"><s:text name="legend.delete"/></div>
      <div id="litem9" class="body"><s:text name="legend.edit"/></div>
      <div id="litem10" class="body"><s:text name="legend.release"/></div>
      <div id="litem11" class="body"><s:text name="legend.buildInSuccess"/></div>
      <div id="litem12" class="body"><s:text name="legend.buildInFailure"/></div>
      <div id="litem13" class="body"><s:text name="legend.buildInError"/></div>
    </div>
  </div>
</div>
</s:i18n>
