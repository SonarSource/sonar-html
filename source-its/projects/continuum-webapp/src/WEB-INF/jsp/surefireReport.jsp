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
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib prefix="c1" uri="continuum" %>
<html>
  <s:i18n name="localization.Continuum">
    <head>
        <title><s:text name="surefireReport.page.title"/></title>
    </head>
    <body>
      <div id="h3">

        <jsp:include page="/WEB-INF/jsp/navigations/ProjectMenu.jsp">
          <jsp:param name="tab" value="view"/>
        </jsp:include>

        <h3>
            <s:text name="surefireReport.section.title">
              <s:param><s:property value="projectName"/></s:param>
              <s:param><s:property value="buildId"/></s:param>
            </s:text>
        </h3>

        <h4><s:text name="surefireReport.summary"/></h4>
        <ec:table items="testSummaryList"
                  var="summary"
                  showExports="false"
                  showPagination="false"
                  showStatusBar="false"
                  sortable="false"
                  filterable="false">
          <ec:row>
            <ec:column property="tests" title="surefireReport.tests"/>
            <ec:column property="errors" title="surefireReport.errors"/>
            <ec:column property="failures" title="surefireReport.failures"/>
            <ec:column property="successRate" title="surefireReport.successRate"/>
            <ec:column property="elapsedTime" title="surefireReport.time"/>
          </ec:row>
        </ec:table>

        <h4><s:text name="surefireReport.packageList"/></h4>
        <ec:table items="testPackageList"
                  var="report"
                  showExports="false"
                  showPagination="false"
                  showStatusBar="false"
                  sortable="false"
                  filterable="false">
          <ec:row>
            <ec:column property="name" title="surefireReport.package">
              <a href="#<c:out value="${pageScope.report.name}"/>"><c:out value="${pageScope.report.name}"/></a>
            </ec:column>
            <ec:column property="tests" title="surefireReport.tests"/>
            <ec:column property="errors" title="surefireReport.errors"/>
            <ec:column property="failures" title="surefireReport.failures"/>
            <ec:column property="successRate" title="surefireReport.successRate"/>
            <ec:column property="elapsedTime" title="surefireReport.time"/>
          </ec:row>
        </ec:table>

        <s:iterator value="testPackageList">
          <h5><a name="<s:property value="name"/>"><s:property value="name"/></a></h5>
          <ec:table items="children"
                    var="report"
                    showExports="false"
                    showPagination="false"
                    showStatusBar="false"
                    sortable="false"
                    filterable="false">
            <ec:row>
              <!-- @todo there must be a better option than to use #attr -->
              <s:if test="#attr.report.errors > 0 || #attr.report.failures > 0">
                <ec:column property="icon" title="&nbsp;" width="1%">
                  <img src="<s:url value="/images/icon_error_sml.gif" includeParams="none"/>" alt="<s:text name="message.error"/>" title="<s:text name="message.error"/>"/>
                </ec:column>
              </s:if>
              <s:else>
                <ec:column property="icon" title="&nbsp;" width="1%">
                  <img src="<s:url value="/images/icon_success_sml.gif" includeParams="none"/>" alt="<s:text name="message.success"/>" title="<s:text name="message.success"/>"/>
                </ec:column>
              </s:else>
              <ec:column property="name" title="surefireReport.class">
                <a href="#<c:out value="${pageScope.report.id}"/>"><c:out value="${pageScope.report.name}"/></a>
              </ec:column>
              <ec:column property="tests" title="surefireReport.tests"/>
              <ec:column property="errors" title="surefireReport.errors"/>
              <ec:column property="failures" title="surefireReport.failures"/>
              <ec:column property="successRate" title="surefireReport.successRate"/>
              <ec:column property="elapsedTime" title="surefireReport.time"/>
            </ec:row>
          </ec:table>
        </s:iterator>

        <h4><s:text name="surefireReport.testCases"/></h4>
        <s:iterator value="testPackageList">
          <s:iterator value="children">
            <h5><a name="<s:property value="id"/>"><s:property value="name"/></a></h5>
            <ec:table items="children"
                      var="testCase"
                      showExports="false"
                      showPagination="false"
                      showStatusBar="false"
                      sortable="false"
                      filterable="false">
              <ec:row>
                <!-- @todo there must be a better option than to use #attr -->
                <s:if test="#attr.testCase.failureType != null">
                  <ec:column property="icon" title="&nbsp;" width="1%">
                    <img src="<s:url value="/images/icon_error_sml.gif" includeParams="none"/>" alt="<s:text name="message.error"/>" title="<s:text name="message.error"/>"/>
                  </ec:column>
                  <ec:column property="name" title="surefireReport.testCase" sortable="false">
                    <c:out value="${pageScope.testCase.name}"/><br/><br/>
                    <pre>
                      <c:out value="${pageScope.testCase.failureDetails}"/>
                    </pre>
                  </ec:column>
                </s:if>
                <s:else>
                  <ec:column property="icon" title="&nbsp;" width="1%">
                    <img src="<s:url value="/images/icon_success_sml.gif" includeParams="none"/>" alt="<s:text name="message.success"/>" title="<s:text name="message.success"/>"/>
                  </ec:column>
                  <ec:column property="name" title="surefireReport.testCase"/>
                </s:else>
                <ec:column property="time" title="surefireReport.time"/>
              </ec:row>
            </ec:table>
          </s:iterator>
        </s:iterator>
      </div>
    </body>
  </s:i18n>
</html>
