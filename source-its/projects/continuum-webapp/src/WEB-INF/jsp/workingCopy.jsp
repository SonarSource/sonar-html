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
<html>
  <s:i18n name="localization.Continuum">
    <head>
        <title><s:text name="workingCopy.page.title"/></title>
    </head>
    <body>
      <div id="h3">

        <jsp:include page="/WEB-INF/jsp/navigations/ProjectMenu.jsp">
          <jsp:param name="tab" value="workingCopy"/>
        </jsp:include>

        <h3>
            <s:text name="workingCopy.section.title">
                <s:param><s:property value="projectName"/></s:param>
            </s:text>
        </h3>

        <s:property value="output" escape="false"/>

        <%
            if ( request.getParameter( "file" ) != null )
            {
        %>
        <p>
        <s:url id="workingCopyTextUrl" action="workingCopyFileText">
          <s:param name="projectId" value="projectId"/>
          <s:param name="projectName" value="projectName"/>
          <s:param name="userDirectory" value="userDirectory"/>
          <s:param name="file" value="file"/>
        </s:url>
        <s:a href="%{workingCopyTextUrl}"><s:text name="workingCopy.currentFile.text"/></s:a>
        </p>
        
        <form>
          <textarea rows="50" cols="100" readonly="true"><s:property value="fileContent"/></textarea>
        </form>
        <%
            }
        %>

      </div>
    </body>
  </s:i18n>
</html>
