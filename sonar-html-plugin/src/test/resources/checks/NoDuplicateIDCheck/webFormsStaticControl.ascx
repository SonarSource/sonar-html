<%@ Control Language="C#" ClientIDMode="Static" %>
<asp:Repeater ID="first" runat="server">
  <ItemTemplate>
    <asp:Label ID="control-static-id" runat="server" />
    <asp:Label ID="generated-id" runat="server" ClientIDMode="AutoID" />
  </ItemTemplate>
</asp:Repeater>

<asp:DetailsView ID="second" runat="server">
  <ItemTemplate>
    <asp:Label ID="control-static-id" runat="server" />
    <asp:Label ID="generated-id" runat="server" ClientIDMode="Predictable" />
  </ItemTemplate>
</asp:DetailsView>
