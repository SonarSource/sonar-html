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

<div id="errorBox" title="Errors">
<div id="samplingParametersError"></div>
</div>