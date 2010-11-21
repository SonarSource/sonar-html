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

<html>
<s:i18n name="localization.Continuum">
  <head>
    <title><s:text name="installationTypeChoice.page.title"/></title>
  </head>
  
  <div id="h3">
    <h3>
      <s:text name="installationTypeChoice.section.title"/>
    </h3>  
    <form action="editInstallation!input.action">
      <div class="axial">

        <table>
          <tbody>    
            <s:select name="installationType" list="installationTypes"
                       label="%{getText('installationTypeChoice.action.label')}" />
          </tbody>
        </table>
        <div class="functnbar3">
          <c1:submitcancel value="%{getText('add')}" cancel="%{getText('cancel')}"/>
        </div>        
      </div>
  
  </div>
  
</s:i18n>
