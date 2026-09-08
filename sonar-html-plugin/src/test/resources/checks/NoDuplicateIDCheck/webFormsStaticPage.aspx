<%@ Page Language="C#" ClientIDMode="Static" %>
<%@ Register TagPrefix="cc" Namespace="System.Web.UI.WebControls" Assembly="System.Web" %>

<asp:Repeater ID="first" runat="server">
  <ItemTemplate>
    <asp:Label ID="page-static-id" runat="server" />
    <asp:Label ID="generated-id" runat="server" ClientIDMode="AutoID" />
  </ItemTemplate>
</asp:Repeater>

<asp:DetailsView ID="second" runat="server">
  <ItemTemplate>
    <asp:Label ID="page-static-id" runat="server" />
    <asp:Label ID="generated-id" runat="server" ClientIDMode="Predictable" />
  </ItemTemplate>
</asp:DetailsView>

<cc:Repeater ID="globallyRegisteredFirst" runat="server" ClientIDMode="AutoID">
  <ItemTemplate>
    <asp:Label ID="globally-registered-generated-id" runat="server" />
  </ItemTemplate>
</cc:Repeater>
<cc:Repeater ID="globallyRegisteredSecond" runat="server" ClientIDMode="Predictable">
  <ItemTemplate>
    <asp:Label ID="globally-registered-generated-id" runat="server" />
  </ItemTemplate>
</cc:Repeater>
