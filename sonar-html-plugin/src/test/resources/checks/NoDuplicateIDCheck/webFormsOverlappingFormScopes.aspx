<asp:FormView ID="form" runat="server">
  <HeaderTemplate>
    <asp:Label ID="form-item-scope" runat="server" />
    <asp:Label ID="form-edit-scope" runat="server" />
    <asp:Label ID="form-insert-scope" runat="server" />
  </HeaderTemplate>
  <ItemTemplate><asp:TextBox ID="form-item-scope" runat="server" /></ItemTemplate>
  <EditItemTemplate><asp:TextBox ID="form-edit-scope" runat="server" /></EditItemTemplate>
  <InsertItemTemplate><asp:TextBox ID="form-insert-scope" runat="server" /></InsertItemTemplate>
</asp:FormView>

<asp:DetailsView ID="details" runat="server">
  <FooterTemplate>
    <asp:Label ID="details-item-scope" runat="server" />
    <asp:Label ID="details-edit-scope" runat="server" />
    <asp:Label ID="details-insert-scope" runat="server" />
  </FooterTemplate>
  <ItemTemplate><asp:TextBox ID="details-item-scope" runat="server" /></ItemTemplate>
  <EditItemTemplate><asp:TextBox ID="details-edit-scope" runat="server" /></EditItemTemplate>
  <InsertItemTemplate><asp:TextBox ID="details-insert-scope" runat="server" /></InsertItemTemplate>
</asp:DetailsView>

<asp:FormView ID="exclusiveModes" runat="server">
  <ItemTemplate><asp:Label ID="exclusive-mode-scope" runat="server" /></ItemTemplate>
  <EditItemTemplate><asp:Label ID="exclusive-mode-scope" runat="server" /></EditItemTemplate>
  <InsertItemTemplate><asp:Label ID="exclusive-mode-scope" runat="server" /></InsertItemTemplate>
</asp:FormView>
