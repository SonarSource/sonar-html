<asp:Repeater ID="first" runat="server">
  <ItemTemplate>
    <asp:Label ID="same-template" runat="server" />
    <asp:TextBox ID="same-template" runat="server" />
  </ItemTemplate>
</asp:Repeater>

<asp:GridView ID="grid" runat="server">
  <asp:TemplateField><ItemTemplate>
    <asp:Label ID="same-item-scope" runat="server" />
  </ItemTemplate></asp:TemplateField>
  <asp:TemplateField><ItemTemplate>
    <asp:TextBox ID="same-item-scope" runat="server" />
  </ItemTemplate></asp:TemplateField>
</asp:GridView>

<asp:Repeater ID="second" runat="server">
  <ItemTemplate>
    <asp:Label ID="static-client-id" runat="server" ClientIDMode="Static" />
  </ItemTemplate>
</asp:Repeater>

<asp:DetailsView ID="details" runat="server">
  <ItemTemplate>
    <asp:Label ID="static-client-id" runat="server" ClientIDMode="Static" />
  </ItemTemplate>
</asp:DetailsView>

<asp:Repeater ID="third" runat="server">
  <ItemTemplate>
    <div id="literal-id"></div>
  </ItemTemplate>
</asp:Repeater>
<asp:DetailsView ID="otherDetails" runat="server">
  <ItemTemplate>
    <div id="literal-id"></div>
  </ItemTemplate>
</asp:DetailsView>

<asp:Repeater ID="staticParent" runat="server" ClientIDMode="Static">
  <ItemTemplate>
    <asp:Label ID="inherited-static-id" runat="server" />
    <asp:Label ID="generated-override" runat="server" ClientIDMode="AutoID" />
  </ItemTemplate>
</asp:Repeater>
<asp:DetailsView ID="otherStaticParent" runat="server" ClientIDMode="Static">
  <ItemTemplate>
    <asp:Label ID="inherited-static-id" runat="server" />
    <asp:Label ID="generated-override" runat="server" ClientIDMode="Predictable" />
  </ItemTemplate>
</asp:DetailsView>

<asp:Label ID="page-scope" runat="server" />
<asp:TextBox ID="page-scope" runat="server" />

<asp:ListView ID="layoutDup" runat="server">
  <LayoutTemplate>
    <asp:Label ID="same-layout-scope" runat="server" />
    <asp:TextBox ID="same-layout-scope" runat="server" />
  </LayoutTemplate>
</asp:ListView>

<asp:FormView ID="formDup" runat="server">
  <HeaderTemplate><asp:Label ID="same-form-scope" runat="server" /></HeaderTemplate>
  <FooterTemplate><asp:TextBox ID="same-form-scope" runat="server" /></FooterTemplate>
</asp:FormView>

<asp:DetailsView ID="detailsDup" runat="server">
  <HeaderTemplate><asp:Label ID="same-details-scope" runat="server" /></HeaderTemplate>
  <PagerTemplate><asp:TextBox ID="same-details-scope" runat="server" /></PagerTemplate>
</asp:DetailsView>

<asp:DataList ID="dataListDup" runat="server">
  <ItemTemplate>
    <asp:Label ID="same-data-list-scope" runat="server" />
    <asp:TextBox ID="same-data-list-scope" runat="server" />
  </ItemTemplate>
</asp:DataList>

<asp:DataGrid ID="dataGridDup" runat="server">
  <ItemTemplate>
    <asp:Label ID="same-data-grid-scope" runat="server" />
    <asp:TextBox ID="same-data-grid-scope" runat="server" />
  </ItemTemplate>
</asp:DataGrid>

<asp:Content ID="contentDup" runat="server">
  <asp:Label ID="same-content-scope" runat="server" />
  <asp:TextBox ID="same-content-scope" runat="server" />
</asp:Content>
