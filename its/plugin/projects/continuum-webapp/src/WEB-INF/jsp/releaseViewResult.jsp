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
        <title><s:text name="releaseProject.page.title"/></title>
    </head>
    <body>
      <h2><s:text name="releaseViewResult.section.title"/></h2>

      <h4><s:text name="releaseViewResult.summary"/></h4>
      <div class="axial">
        <table border="1" cellspacing="2" cellpadding="3" width="100%">
          <c1:data label="%{getText('releaseViewResult.projectName')}">
            <s:param name="after"><s:property value="projectName"/></s:param>
          </c1:data>
          <c1:data label="%{getText('releaseViewResult.releaseGoal')}">
            <s:param name="after"><s:property value="releaseGoal"/></s:param>
          </c1:data>
          <c1:data label="%{getText('releaseViewResult.startTime')}">
              <s:param name="after"><c1:date name="result.startTime"/></s:param>
          </c1:data>
          <c1:data label="%{getText('releaseViewResult.endTime')}">
              <s:param name="after"><c1:date name="result.endTime"/></s:param>
          </c1:data>
          <c1:data label="%{getText('releaseViewResult.state')}">
            <s:param name="after">
              <s:if test="result.resultCode == 0">
                <s:text name="releaseViewResult.success"/>
              </s:if>
              <s:else>
                <s:text name="releaseViewResult.error"/>
              </s:else>
            </s:param>
          </c1:data>
          <c1:data label="%{getText('releaseViewResult.username')}">
              <s:param name="after"><s:property value="username"/></s:param>
          </c1:data>
        </table>
      </div>

      <h4><s:text name="releaseViewResult.output"/></h4>
      <p>
        <s:if test="result.output == ''">
            <s:text name="releaseViewResult.noOutput"/>
        </s:if>
        <s:else>
          <div style="width:100%; height:500px; overflow:auto; border-style: solid; border-width: 1px">
            <pre><s:property value="result.output"/></pre>
          </div>
        </s:else>
      </p>
      <input type="button" value="<s:text name="back"/>" onClick="history.go(-1)">

    </body>
  </s:i18n>
</html>
