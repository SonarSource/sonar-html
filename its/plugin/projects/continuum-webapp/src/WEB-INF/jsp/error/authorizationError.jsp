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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<s:i18n name="localization.Continuum">
<head>
  <title><s:text name="authorizationError.page.title"/></title>
</head>

<body>
  <div id="h3">
    <h3><s:text name="authorizationError.section.title"/></h3>
    <div class="errors">
      <s:if test="hasActionErrors()">
        <s:actionerror/>
      </s:if>
      <s:else>
        <s:text name="authorizationError.not.authorized"/>
      </s:else>
    </div>
  </div>
</body>
</s:i18n>
</html>
