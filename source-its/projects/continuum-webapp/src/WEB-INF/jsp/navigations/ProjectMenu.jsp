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
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="continuum" prefix="c1" %>

<div>
  <p style="border-top: 1px solid transparent; border-bottom: 1px solid #DFDEDE;">

    <s:url id="projectGroupSummaryUrl" action="projectGroupSummary" includeParams="none">
        <s:param name="projectGroupId" value="project.projectGroup.id"/>
    </s:url>
    <s:url id="viewUrl" action="projectView" includeParams="none">
        <s:param name="projectId" value="projectId"/>
        <s:param name="tab" value="view"/>
        <s:param name="projectGroupId" value="project.projectGroup.id"/>
    </s:url>
    <s:url id="buildResultsUrl" action="buildResults" includeParams="none">
        <s:param name="projectId" value="projectId"/>
        <s:param name="tab" value="buildResults"/>
        <s:param name="projectGroupId" value="project.projectGroup.id"/>
    </s:url>
    <s:url id="workingCopyUrl" action="workingCopy" includeParams="none">
        <s:param name="projectId" value="projectId"/>
        <s:param name="tab" value="workingCopy"/>
        <s:param name="projectGroupId" value="project.projectGroup.id"/>
    </s:url>

    <a style="border: 1px solid #DFDEDE; padding-left: 1em; padding-right: 1em; text-decoration: none;" href="${projectGroupSummaryUrl}"><s:text name="projectGroup.tab.summary"/></a>
    <c:choose>
      <c:when test="${param.tab == 'view'}">
        <b style="border: 1px solid #DFDEDE; padding-left: 1em; padding-right: 1em;"><s:text name="info"/></b>
      </c:when>
      <c:otherwise>
        <a style="border: 1px solid #DFDEDE; padding-left: 1em; padding-right: 1em; text-decoration: none;" href="${viewUrl}"><s:text name="info"/></a>
      </c:otherwise>
    </c:choose>

    <c:choose>
      <c:when test="${param.tab == 'buildResults'}">
        <b style="border: 1px solid #DFDEDE; padding-left: 1em; padding-right: 1em;"><s:text name="builds"/></b>
      </c:when>
      <c:otherwise>
        <a style="border: 1px solid #DFDEDE; padding-left: 1em; padding-right: 1em; text-decoration: none;" href="${buildResultsUrl}"><s:text name="builds"/></a>
      </c:otherwise>
    </c:choose>

    <c:choose>
      <c:when test="${param.tab == 'workingCopy'}">
        <b style="border: 1px solid #DFDEDE; padding-left: 1em; padding-right: 1em;"><s:text name="workingCopy"/></b>
      </c:when>
      <c:otherwise>
        <a style="border: 1px solid #DFDEDE; padding-left: 1em; padding-right: 1em; text-decoration: none;" href="${workingCopyUrl}"><s:text name="workingCopy"/></a>
      </c:otherwise>
    </c:choose>

  </p>
</div>
