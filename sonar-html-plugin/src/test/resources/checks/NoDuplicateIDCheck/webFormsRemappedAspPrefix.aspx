<%@ Register TagPrefix="asp" Namespace="Example.Controls" Assembly="Example.Controls" %>

<asp:Repeater ID="firstCustomRepeater" runat="server">
  <ItemTemplate>
    <div ID="remapped-asp-prefix-id" runat="server"></div>
  </ItemTemplate>
</asp:Repeater>
<asp:Repeater ID="secondCustomRepeater" runat="server">
  <ItemTemplate>
    <div ID="remapped-asp-prefix-id" runat="server"></div>
  </ItemTemplate>
</asp:Repeater>
