<%@ include file="/WEB-INF/jsp/include/taglibs.jspf" %>

<script type="text/javascript">
function validateForm() {
var errStr = "";

var errorBox = document.getElementById("errorBox");
var riskAssessment = document.getElementById("riskAssessment").value;
var samplePercentage = document.getElementById("samplePercentage").value;
var minimumSampleSize = document.getElementById("minimumSampleSize").value;
var maximumSampleSize = document.getElementById("maximumSampleSize").value;

if (riskAssessment == "") { errStr += "<spring:message code="error.samplingParameters.riskAssessment.mandatory" /><br />"; }

if (samplePercentage == "" || samplePercentage < 0 || samplePercentage > 100 || !isInteger(samplePercentage)) { errStr += "<spring:message code="error.samplingParameters.invalidField" arguments="Review Sample Size %" /><br />"; }

if (minimumSampleSize == "" || minimumSampleSize < 0 || !isInteger(minimumSampleSize)) { errStr += "<spring:message code="error.samplingParameters.invalidField" arguments="Minimum Sample Size" /><br />"; }

if (maximumSampleSize == "" || maximumSampleSize < 0 || !isInteger(maximumSampleSize)) { errStr += "<spring:message code="error.samplingParameters.invalidField" arguments="Maximum Sample Size" /><br />"; }

if (isInteger(minimumSampleSize) && isInteger(maximumSampleSize) && parseInt(maximumSampleSize) < parseInt(minimumSampleSize)) { errStr += "<spring:message code="error.samplingParameters.invalidField" arguments="Minimum/Maximum Sample Size" /><br />"; }

if (errStr != "") { jQuery('#samplingParametersError').html(errStr); jQuery("#errorBox").dialog('open'); return false; }
return true;
}

function isInteger(val)
{
var i;

if (val == null || val.length == 0) { return false; }
for (i = 0; i < val.length; i++)
{
var c = val.charAt;
if (!((c >= "0") && (c <= "9"))) { return false; }
}
return true;
}

jQuery(document).ready(function($) {
$('#errorBox').dialog({ autoOpen: false, bgiframe: true, resizable: false, modal: true, buttons: { "Ok": function() {$(this).dialog("close"); } } });
});

</script>

<form:form id="samplingParametersForm" modelAttribute="command" onsubmit="return validateForm()" method="POST">
<c:choose>
<c:when test="${command.hierarchy.hierarchyDepth != 2}">
<div id="depthError" align="center" style="font-style: italic; font-size: xx-small;">
<spring:message code="maintainSamplingParameters.depth.error"/>
</div>
</c:when>
<c:otherwise>
<div style="padding-left: 10px;" id="breadcrumbs">${command.hierarchy.path}</div>
<br/>
<table id="samplingParametersForHierarchy">
<tr>
<td width="40%"><spring:message code="maintainSamplingParameters.label.riskAssessment"/></td>
<td>
<form:select path="riskAssessment" id="riskAssessment">
<form:option value=""><spring:message code="maintainSamplingParameters.select.defaultRisk"/></form:option>
<form:options items="${riskAssessments}" itemLabel="riskAssessment"/>
</form:select>
<form:hidden path="version" id="version"/>
<form:hidden path="hierarchy" id="hierarchy"/>
</td>
</tr>
<tr>
<td><spring:message code="maintainSamplingParameters.label.samplePercentage"/></td>
<td><form:input size="16" path="samplePercentage" id="samplePercentage" maxlength="3"/></td>
</tr>
<tr>
<td><spring:message code="maintainSamplingParameters.label.minimumSampleSize"/></td>
<td><form:input size="16" path="minimumSampleSize" id="minimumSampleSize"/></td>
</tr>
<tr>
<td><spring:message code="maintainSamplingParameters.label.maximumSampleSize"/></td>
<td><form:input size="16" path="maximumSampleSize" id="maximumSampleSize"/></td>
</tr>
<tr>
</tr>
</table>
</c:otherwise>
</c:choose>
</form:form>

<div id="errorBox" title="Errors">
<div id="samplingParametersError"></div>
</div>