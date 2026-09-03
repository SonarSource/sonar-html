<cc:Repeater ID="lateRegisterFirst" runat="server" ClientIDMode="AutoID">
  <ItemTemplate>
    <asp:Label ID="late-register-id" runat="server" />
  </ItemTemplate>
</cc:Repeater>
<cc:Repeater ID="lateRegisterSecond" runat="server" ClientIDMode="AutoID">
  <ItemTemplate>
    <asp:TextBox ID="late-register-id" runat="server" />
  </ItemTemplate>
</cc:Repeater>

<asp:Repeater ID="latePageFirst" runat="server">
  <ItemTemplate>
    <asp:Label ID="late-page-mode-id" runat="server" />
  </ItemTemplate>
</asp:Repeater>
<asp:Repeater ID="latePageSecond" runat="server">
  <ItemTemplate>
    <asp:TextBox ID="late-page-mode-id" runat="server" />
  </ItemTemplate>
</asp:Repeater>

<%@ Register TagPrefix="cc" Namespace="System.Web.UI.WebControls" Assembly="System.Web" %>
<%@ Page ClientIDMode="Static" %>
