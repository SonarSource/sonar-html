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
  <ItemTemplate><asp:TextBox ID="same-form-scope" runat="server" /></ItemTemplate>
</asp:FormView>

<asp:DetailsView ID="detailsDup" runat="server">
  <FooterTemplate><asp:Label ID="same-details-scope" runat="server" /></FooterTemplate>
  <ItemTemplate><asp:TextBox ID="same-details-scope" runat="server" /></ItemTemplate>
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

<asp:Repeater ID="staticThroughPanel" runat="server" ClientIDMode="Static">
  <ItemTemplate>
    <asp:Panel ID="generatedPanel" runat="server" ClientIDMode="AutoID">
      <asp:Label ID="naming-container-inheritance-id" runat="server" />
    </asp:Panel>
  </ItemTemplate>
</asp:Repeater>
<asp:DetailsView ID="otherStaticThroughPanel" runat="server" ClientIDMode="Static">
  <ItemTemplate>
    <asp:Panel ID="otherGeneratedPanel" runat="server" ClientIDMode="Predictable">
      <asp:Label ID="naming-container-inheritance-id" runat="server" />
    </asp:Panel>
  </ItemTemplate>
</asp:DetailsView>

<repeater runat="server">
  <asp:Label ID="unprefixed-container-id" runat="server" />
</repeater>
<repeater runat="server">
  <asp:TextBox ID="unprefixed-container-id" runat="server" />
</repeater>

<asp:LoginView ID="staticLoginView" runat="server" ClientIDMode="Static">
  <LoggedInTemplate>
    <asp:Repeater ID="loginFirst" runat="server">
      <ItemTemplate><asp:Label ID="loginview-static-id" runat="server" /></ItemTemplate>
    </asp:Repeater>
    <asp:Repeater ID="loginSecond" runat="server">
      <ItemTemplate><asp:TextBox ID="loginview-static-id" runat="server" /></ItemTemplate>
    </asp:Repeater>
  </LoggedInTemplate>
</asp:LoginView>

<asp:Wizard ID="staticWizard" runat="server" ClientIDMode="Static">
  <WizardSteps>
    <asp:WizardStep ID="step" runat="server">
      <asp:Repeater ID="wizardFirst" runat="server">
        <ItemTemplate><asp:Label ID="wizard-static-id" runat="server" /></ItemTemplate>
      </asp:Repeater>
      <asp:Repeater ID="wizardSecond" runat="server">
        <ItemTemplate><asp:TextBox ID="wizard-static-id" runat="server" /></ItemTemplate>
      </asp:Repeater>
    </asp:WizardStep>
  </WizardSteps>
</asp:Wizard>

<asp:Menu ID="sameMenuTemplateScope" runat="server">
  <StaticItemTemplate>
    <asp:Label ID="same-menu-template-scope" runat="server" />
    <asp:TextBox ID="same-menu-template-scope" runat="server" />
  </StaticItemTemplate>
</asp:Menu>
<asp:SiteMapPath ID="sameSiteMapTemplateScope" runat="server">
  <NodeTemplate>
    <asp:Label ID="same-site-map-template-scope" runat="server" />
    <asp:TextBox ID="same-site-map-template-scope" runat="server" />
  </NodeTemplate>
</asp:SiteMapPath>
