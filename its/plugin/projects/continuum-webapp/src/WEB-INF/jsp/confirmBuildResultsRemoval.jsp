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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
  <s:i18n name="localization.Continuum">
    <head>
        <title><s:text name="buildResult.delete.confirmation.page.title"/></title>
    </head>
    <body>
      <div id="axial" class="h3">
        <h3><s:text name="buildResult.delete.confirmation.section.title"/></h3>
        <div class="axial">
        <s:if test="hasActionMessages()">
          <div class="warningmessage">
            <p>
              <s:actionmessage/>
            </p>        
          </div>
        </s:if>
        <!-- in this case we come from the build result edit -->
        <s:if test="buildId">
          <c:set var="action" value="removeBuildResult.action" />
        </s:if>
        <s:else>
          <c:set var="action" value="removeBuildResults.action" />
        </s:else>
        <form action="${action}" method="post">
          <s:hidden name="projectGroupId"/>
          <s:hidden name="projectId"/>
          <s:hidden name="buildId"/>
          <s:hidden name="confirmed" value="true"/>
          <s:if test="selectedBuildResults">
          <s:iterator value="selectedBuildResults">
            <input type="hidden" value="<s:property/>" name="selectedBuildResults" />
          </s:iterator>
          </s:if>
          <s:else>
            <input type="hidden" value="<s:property value="buildId"/>" name="selectedBuildResults" />
          </s:else>
          
          <s:actionerror/>

          <div class="warningmessage">
            <p>
              <strong>
                <s:text name="buildResult.delete.confirmation.message">
                  <s:param><s:property value="%{selectedBuildResults.size}"/></s:param>
                </s:text>
              </strong>
            </p>
          </div>

          <div class="functnbar3">
            <s:if test="buildId > 0">
              <c1:submitcancel value="%{getText('delete')}" cancel="%{getText('cancel')}"/>
            </s:if>
            <s:elseif test="selectedBuildResults.size > 0">
              <c1:submitcancel value="%{getText('delete')}" cancel="%{getText('cancel')}"/>
            </s:elseif>
            <s:else>
              <input type="submit" value="<s:text name="cancel"/>" onClick="history.back()"/>
            </s:else>
          </div>
        </form>
        </div>
      </div>
    </body>
  </s:i18n>
</html>
