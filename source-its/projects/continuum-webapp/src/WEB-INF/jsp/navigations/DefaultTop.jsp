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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://plexus.codehaus.org/redback/taglib-1.0" prefix="redback" %>

<s:i18n name="localization.Continuum">
  <div id="banner">
    <span id="bannerLeft">
      <a href="http://continuum.apache.org">
        <img src="<s:url value="/images/continuum_logo_75.gif" includeParams="none"/>" alt="Continuum" title="Continuum" border="0">
      </a>
    </span>
    <span id="bannerRight">
      <s:action name="companyInfo" executeResult="true"/>
    </span>

    <div class="clear">
      <hr/>
    </div>
  </div>

  <div id="breadcrumbs">

    <div style="float: right;">
      <a href="http://continuum.apache.org">Continuum</a> |
      <a href="http://maven.apache.org/">Maven</a> |
      <a href="http://www.apache.org/">Apache</a>
    </div>
    <%@ include file="/WEB-INF/jsp/redback/include/securityLinks.jsp" %>
  </div>
</s:i18n>
