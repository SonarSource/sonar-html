<%@ Register TagPrefix="uc" TagName="Card" Src="~/Card.ascx" %>
<%@ Register TagPrefix="uc" TagName="Repeater" Src="~/Repeater.ascx" %>

<uc:Card ID="firstCard" runat="server">
  <asp:Label ID="registered-user-control-id" runat="server" />
</uc:Card>
<uc:Card ID="secondCard" runat="server">
  <asp:Label ID="registered-user-control-id" runat="server" />
</uc:Card>

<uc:Card ID="duplicateWithinCard" runat="server">
  <asp:Label ID="same-user-control-scope" runat="server" />
  <asp:TextBox ID="same-user-control-scope" runat="server" />
</uc:Card>

<uc:Repeater ID="userControlNamedLikeBuiltIn" runat="server">
  <ItemTemplate>
    <asp:Label ID="same-built-in-named-user-control-scope" runat="server" />
  </ItemTemplate>
  <HeaderTemplate>
    <asp:TextBox ID="same-built-in-named-user-control-scope" runat="server" />
  </HeaderTemplate>
</uc:Repeater>
