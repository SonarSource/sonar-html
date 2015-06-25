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
    <title>
      <s:text name="installation.page.title"/>
    </title>
  </head>

  <body>
  <div id="axial" class="h3">
    <h3>
      <s:text name="installation.section.title"/>
    </h3>

    <s:form action="saveInstallation!save" method="post">

      <s:if test="hasActionErrors()">
        <h3>Action Error</h3>
      </s:if>
      <p>
        <s:actionerror/>
      </p>

      <div class="axial">

        <table>
          <tbody>
            <s:hidden name="installation.installationId" />
            <s:hidden name="installationType" />
            <s:hidden name="displayTypes" />
            <s:hidden name="varNameUpdatable" />
            <s:hidden name="varNameDisplayable" />
            <s:textfield label="%{getText('installation.name.label')}" name="installation.name"
                            required="true"/>
            <s:if test="displayTypes">
              <s:select label="%{getText('installation.type.label')}" name="installation.type" list="typesLabels" />
            </s:if>
            <s:if test="varNameUpdatable">
              <s:if test="varNameDisplayable">
                <s:textfield label="%{getText('installation.varName.label')}" name="installation.varName" required="true" />
              </s:if>
            </s:if>
            <s:else>
              <s:if test="varNameDisplayable">
                <s:textfield label="%{getText('installation.varName.label')}" name="installation.varName" required="true" readonly="true"/>
              </s:if>
            </s:else>
            <s:textfield label="%{getText('installation.value.label')}" name="installation.varValue"
                          required="true"/>
            <s:if test="%{(automaticProfileDisplayable && installation == null) || (installation.installationId == 0)}">
              <s:checkbox label="%{getText('installation.automaticProfile.label')}" name="automaticProfile" />
            </s:if>
          </tbody>
        </table>
        <div class="functnbar3">
          <c1:submitcancel value="%{getText('save')}" cancel="%{getText('cancel')}"/>
        </div>

      </div>
    </s:form>
  </div>
  </body>
</s:i18n>
</html>
