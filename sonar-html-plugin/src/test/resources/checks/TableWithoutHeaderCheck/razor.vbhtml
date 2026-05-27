<!-- Compliant: vbhtml Razor view, body comes from the child view -->
<table>
  @RenderBody()
</table>

<!-- Non-Compliant: vbhtml without fragment rendering -->
<table>
  <tr><td>value</td></tr>
</table>
