<%@ Register TagPrefix="uc" TagName="Card" Src="~/Card.ascx" %>

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
