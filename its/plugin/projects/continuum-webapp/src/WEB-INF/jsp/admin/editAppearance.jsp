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
  <title><s:text name="appearance.page.title"/></title>
</head>

<body>
  <h1><s:text name="appearance.section.title"/></h1>

  <h2><s:text name="appearance.companyDetails"/></h2>

  <p>
    <s:text name="appearance.enterCompanyPom"/>
  </p>

  <s:actionmessage/>
  <s:form method="post" action="saveAppearance" namespace="/admin" validate="true" theme="xhtml">
    <s:textfield name="companyPom.groupId" label="%{getText('appearance.companyPom.groupId')}"/>
    <s:textfield name="companyPom.artifactId" label="%{getText('appearance.companyPom.artifactId')}"/>
    <s:submit value="%{getText('save')}"/>
  </s:form>
</body>
</s:i18n>
</html>
