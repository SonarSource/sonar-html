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
<%@ taglib prefix="c1" uri="continuum" %>
<html>
  <s:i18n name="localization.Continuum">
    <head>
      <title><s:text name="scmResult.page.title"/></title>
    </head>
    <body>
      <div id="h3">
        <s:action name="projectGroupTab" executeResult="true"/>
        <h3>
          <s:text name="scmResult.section.title"/>
        </h3>
        <div class="axial">
          <table border="1" cellspacing="2" cellpadding="3" width="100%">
            <c1:data label="%{getText('scmResult.projectGroupName')}" name="projectGroupName"/>
            <c1:data label="%{getText('scmResult.scmRootAddress')}" name="projectScmRoot.scmRootAddress"/>
            <c1:data label="%{getText('scmResult.state')}" name="state"/>
          </table>
        </div>
        
        <h4><s:text name="scmResult.scmError"/></h4>
        <div style="width:100%; height:500px; overflow:auto; border-style: solid; border-width: 1px">
          <pre><s:property value="projectScmRoot.error"/></pre>
        </div>
      </div>
    </body>
  </s:i18n>
</html>
