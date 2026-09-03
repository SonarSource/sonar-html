<%@ Register TagPrefix="custom" Namespace="Example.Controls" Assembly="Example.Controls" %>

<custom:Repeater ID="first" runat="server">
  <ItemTemplate>
    <asp:Label ID="custom-non-container-id" runat="server" />
  </ItemTemplate>
</custom:Repeater>
<custom:Repeater ID="second" runat="server">
  <ItemTemplate>
    <asp:TextBox ID="custom-non-container-id" runat="server" />
  </ItemTemplate>
</custom:Repeater>
