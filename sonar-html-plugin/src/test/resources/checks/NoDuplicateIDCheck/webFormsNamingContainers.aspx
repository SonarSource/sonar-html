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
