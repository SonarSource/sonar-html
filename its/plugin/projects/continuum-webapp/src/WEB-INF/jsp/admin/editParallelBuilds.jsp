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

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="continuum" prefix="c1" %>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<%@ taglib uri="http://plexus.codehaus.org/redback/taglib-1.0" prefix="redback" %>

<html>
  <s:i18n name="localization.Continuum">
<head>
<title><s:text name="parallel.build.queue.page.title"/></title>
</head>
<body>

<div class="app">
  <div id="axial" class="h3">
    <h3><s:text name="parallel.build.queue.section.title"/></h3>

    <c:if test="${!empty actionErrors}">
      <div class="errormessage">
        <p><s:actionerror/></p>
      </div>
    </c:if>
    
    <div class="axial">
      <s:form action="saveBuildQueue" method="post" validate="true">
          <table>
            <s:textfield label="%{getText('parallel.build.queue.name')}" name="name" required="true" />
          </table>
          <div class="functnbar3">
            <c1:submitcancel value="%{getText('save')}" cancel="%{getText('cancel')}"/>
          </div>
      </s:form>
    </div>
  </div>
</div>
</body>
</s:i18n>
</html>