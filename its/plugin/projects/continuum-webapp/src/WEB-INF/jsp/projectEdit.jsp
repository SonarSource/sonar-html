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
<%@ taglib uri="continuum" prefix="c1" %>
<html>
  <s:i18n name="localization.Continuum">
    <head>
      <title><s:text name="projectEdit.page.title"/></title>
      <script type="text/javascript">
        function focus()
        {
          checkUseCache();
        }

        function checkUseCache()
        {
          var form = document.forms[ "editProject" ];

          if ( form.scmUrl.value.toLowerCase().indexOf( "scm:svn:" ) == 0 )
          {
            form.scmUseCache.disabled = false;
            form.scmUsername.disabled = form.scmUseCache.checked;
            form.scmPassword.disabled = form.scmUseCache.checked;
          }
          else
          {
            form.scmUseCache.disabled = true;
            form.scmUsername.disabled = false;
            form.scmPassword.disabled = false;
          }
        }
      </script>
    </head>
    <body>
      <div id="axial" class="h3">
        <h3><s:text name="projectEdit.section.title"/></h3>

        <div class="axial">
          <s:form name="editProject" action="projectSave" method="post" validate="true">
            <table>
              <tbody>
                <s:textfield label="%{getText('projectEdit.project.name.label')}" name="name" required="true"/>
                <s:textfield label="%{getText('projectEdit.project.version.label')}" name="version" required="true"/>
                <s:textfield label="%{getText('projectEdit.project.scmUrl.label')}" name="scmUrl" required="true"
                             onchange="checkUseCache()"/>
                <s:checkbox label="%{getText('projectEdit.project.scmUseCache.label')}" name="scmUseCache"
                             onclick="checkUseCache()"/>
                <s:textfield label="%{getText('projectEdit.project.scmUsername.label')}" name="scmUsername"/>
                <s:password label="%{getText('projectEdit.project.scmPassword.label')}" name="scmPassword"/>
                <s:textfield label="%{getText('projectEdit.project.scmTag.label')}" name="scmTag"/>
              </tbody>
            </table>
            <div class="functnbar3">
              <c1:submitcancel value="%{getText('save')}" cancel="%{getText('cancel')}"/>
            </div>
            <s:hidden name="projectId"/>
          </s:form>
        </div>
      </div>
    </body>
  </s:i18n>
</html>