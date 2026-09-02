<asp:GridView ID="orders" runat="server">
  <Columns>
    <asp:TemplateField>
      <ItemTemplate>
        <asp:Label ID="status" runat="server" />
        <asp:Repeater ID="lines" runat="server">
          <ItemTemplate>
            <asp:Label ID="status" runat="server" />
            <div ID="server-html" runat="server"></div>
          </ItemTemplate>
        </asp:Repeater>
      </ItemTemplate>
      <EditItemTemplate>
        <asp:Label ID="status" runat="server" />
      </EditItemTemplate>
    </asp:TemplateField>
  </Columns>
</asp:GridView>

<asp:Repeater ID="customers" runat="server">
  <ItemTemplate>
    <asp:Label ID="status" runat="server" />
  </ItemTemplate>
</asp:Repeater>

<asp:DetailsView ID="customerDetails" runat="server">
  <Fields>
    <asp:TemplateField>
      <ItemTemplate>
        <asp:Label ID="status" runat="server" />
        <div ID="server-html" runat="server"></div>
      </ItemTemplate>
      <EditItemTemplate>
        <asp:Label ID="status" runat="server" />
      </EditItemTemplate>
      <InsertItemTemplate>
        <asp:Label ID="status" runat="server" />
      </InsertItemTemplate>
    </asp:TemplateField>
  </Fields>
</asp:DetailsView>

<asp:Repeater ID="templateScopes" runat="server">
  <HeaderTemplate><asp:Label ID="template-id" runat="server" /></HeaderTemplate>
  <FooterTemplate><asp:Label ID="template-id" runat="server" /></FooterTemplate>
  <ItemTemplate><asp:Label ID="template-id" runat="server" /></ItemTemplate>
  <AlternatingItemTemplate><asp:Label ID="template-id" runat="server" /></AlternatingItemTemplate>
  <SeparatorTemplate><asp:Label ID="template-id" runat="server" /></SeparatorTemplate>
</asp:Repeater>

<asp:FormView ID="formTemplates" runat="server">
  <EmptyDataTemplate><asp:Label ID="template-id" runat="server" /></EmptyDataTemplate>
  <PagerTemplate><asp:Label ID="template-id" runat="server" /></PagerTemplate>
</asp:FormView>

<asp:ListView ID="listTemplates" runat="server">
  <EmptyItemTemplate><asp:Label ID="template-id" runat="server" /></EmptyItemTemplate>
  <SelectedItemTemplate><asp:Label ID="template-id" runat="server" /></SelectedItemTemplate>
  <GroupTemplate><asp:Label ID="template-id" runat="server" /></GroupTemplate>
  <GroupSeparatorTemplate><asp:Label ID="template-id" runat="server" /></GroupSeparatorTemplate>
  <ItemSeparatorTemplate><asp:Label ID="template-id" runat="server" /></ItemSeparatorTemplate>
  <LayoutTemplate><asp:Label ID="template-id" runat="server" /></LayoutTemplate>
</asp:ListView>

<asp:ListView ID="list" runat="server">
  <ItemTemplate><asp:Label ID="container-id" runat="server" /></ItemTemplate>
</asp:ListView>
<asp:FormView ID="form" runat="server">
  <ItemTemplate><asp:Label ID="container-id" runat="server" /></ItemTemplate>
</asp:FormView>
<asp:DataList ID="dataList" runat="server">
  <ItemTemplate><asp:Label ID="container-id" runat="server" /></ItemTemplate>
</asp:DataList>
<asp:DataGrid ID="dataGrid" runat="server">
  <ItemTemplate><asp:Label ID="container-id" runat="server" /></ItemTemplate>
</asp:DataGrid>
<asp:Content ID="firstContent" runat="server">
  <asp:Label ID="container-id" runat="server" />
</asp:Content>
<asp:Content ID="secondContent" runat="server">
  <asp:Label ID="container-id" runat="server" />
</asp:Content>
<asp:ListView runat="server">
  <ItemTemplate><asp:Label ID="identity-scope-id" runat="server" /></ItemTemplate>
</asp:ListView>

<asp:FormView ID="formPagerScope" runat="server">
  <FooterTemplate><asp:Label ID="form-pager-scope-id" runat="server" /></FooterTemplate>
  <PagerTemplate><asp:Label ID="form-pager-scope-id" runat="server" /></PagerTemplate>
</asp:FormView>
<asp:DetailsView ID="detailsPagerScope" runat="server">
  <HeaderTemplate><asp:Label ID="details-pager-scope-id" runat="server" /></HeaderTemplate>
  <PagerTemplate><asp:Label ID="details-pager-scope-id" runat="server" /></PagerTemplate>
</asp:DetailsView>

<asp:Repeater ID="generatedThroughPanel" runat="server" ClientIDMode="AutoID">
  <ItemTemplate>
    <asp:Panel ID="staticPanel" runat="server" ClientIDMode="Static">
      <asp:Label ID="panel-inheritance-id" runat="server" />
    </asp:Panel>
  </ItemTemplate>
</asp:Repeater>
<asp:DetailsView ID="otherGeneratedThroughPanel" runat="server" ClientIDMode="Predictable">
  <ItemTemplate>
    <asp:Panel ID="otherStaticPanel" runat="server" ClientIDMode="Static">
      <asp:Label ID="panel-inheritance-id" runat="server" />
    </asp:Panel>
  </ItemTemplate>
</asp:DetailsView>
<asp:ListView runat="server">
  <ItemTemplate><asp:Label ID="identity-scope-id" runat="server" /></ItemTemplate>
</asp:ListView>
