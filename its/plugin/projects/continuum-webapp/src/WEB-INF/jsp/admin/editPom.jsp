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

<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<s:i18n name="localization.Continuum">
<head>
  <title><s:text name="companyPom.page.title"/></title>
</head>

<body>
<h1><s:text name="companyPom.section.title"/></h1>

<s:actionmessage/>
<s:form method="post" action="saveCompanyPom" namespace="/admin" validate="true" theme="xhtml">
  <s:label name="companyModel.groupId" label="%{getText('appearance.companyPom.groupId')}"/>
  <s:label name="companyModel.artifactId" label="%{getText('appearance.companyPom.artifactId')}"/>
  <tr>
    <td><s:text name="appearance.companyPom.version"/></td>
    <td>
      <s:property value="companyModel.version"/>
      <i>(<s:text name="companyPom.autoIncrementVersion"/>)</i>
    </td>
  </tr>
  <tr>
    <td></td>
    <td><h2><s:text name="companyPom.organization"/></h2></td>
  </tr>
  <s:textfield name="companyModel.organization.name" size="40" label="%{getText('appearance.companyPom.organizationName.label')}"/>
  <s:textfield name="companyModel.organization.url" size="70" label="%{getText('appearance.companyPom.organizationUrl.label')}"/>
  <%-- TODO: how to get it to be a string, not a String[]? --%>
  <s:textfield name="companyModel.properties['organization.logo']" size="70" label="%{getText('appearance.companyPom.organizationLogoUrl.label')}"/>
  <s:submit value="%{getText('save')}"/>
</s:form>

</body>
</s:i18n>
</html>
