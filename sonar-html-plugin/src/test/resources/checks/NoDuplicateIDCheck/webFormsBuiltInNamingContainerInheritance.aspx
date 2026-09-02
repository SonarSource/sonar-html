<asp:CreateUserWizard ID="staticCreateUserWizard" runat="server" ClientIDMode="Static">
  <WizardSteps>
    <asp:CreateUserWizardStep ID="createUserStep" runat="server">
      <ContentTemplate>
        <asp:Repeater ID="createUserFirst" runat="server">
          <ItemTemplate><asp:Label ID="create-user-wizard-static-id" runat="server" /></ItemTemplate>
        </asp:Repeater>
        <asp:Repeater ID="createUserSecond" runat="server">
          <ItemTemplate><asp:TextBox ID="create-user-wizard-static-id" runat="server" /></ItemTemplate>
        </asp:Repeater>
      </ContentTemplate>
    </asp:CreateUserWizardStep>
  </WizardSteps>
</asp:CreateUserWizard>

<asp:ChangePassword ID="staticChangePassword" runat="server" ClientIDMode="Static">
  <ChangePasswordTemplate>
    <asp:Repeater ID="changePasswordFirst" runat="server">
      <ItemTemplate><asp:Label ID="change-password-static-id" runat="server" /></ItemTemplate>
    </asp:Repeater>
    <asp:Repeater ID="changePasswordSecond" runat="server">
      <ItemTemplate><asp:TextBox ID="change-password-static-id" runat="server" /></ItemTemplate>
    </asp:Repeater>
  </ChangePasswordTemplate>
</asp:ChangePassword>

<asp:Login ID="staticLogin" runat="server" ClientIDMode="Static">
  <LayoutTemplate>
    <asp:Repeater ID="loginFirst" runat="server">
      <ItemTemplate><asp:Label ID="login-static-id" runat="server" /></ItemTemplate>
    </asp:Repeater>
    <asp:Repeater ID="loginSecond" runat="server">
      <ItemTemplate><asp:TextBox ID="login-static-id" runat="server" /></ItemTemplate>
    </asp:Repeater>
  </LayoutTemplate>
</asp:Login>

<asp:PasswordRecovery ID="staticPasswordRecovery" runat="server" ClientIDMode="Static">
  <UserNameTemplate>
    <asp:Repeater ID="passwordRecoveryFirst" runat="server">
      <ItemTemplate><asp:Label ID="password-recovery-static-id" runat="server" /></ItemTemplate>
    </asp:Repeater>
    <asp:Repeater ID="passwordRecoverySecond" runat="server">
      <ItemTemplate><asp:TextBox ID="password-recovery-static-id" runat="server" /></ItemTemplate>
    </asp:Repeater>
  </UserNameTemplate>
</asp:PasswordRecovery>

<asp:Menu ID="staticMenu" runat="server" ClientIDMode="Static">
  <StaticItemTemplate>
    <asp:Repeater ID="menuFirst" runat="server">
      <ItemTemplate><asp:Label ID="menu-static-id" runat="server" /></ItemTemplate>
    </asp:Repeater>
    <asp:Repeater ID="menuSecond" runat="server">
      <ItemTemplate><asp:TextBox ID="menu-static-id" runat="server" /></ItemTemplate>
    </asp:Repeater>
  </StaticItemTemplate>
</asp:Menu>

<asp:SiteMapPath ID="staticSiteMapPath" runat="server" ClientIDMode="Static">
  <NodeTemplate>
    <asp:Repeater ID="siteMapFirst" runat="server">
      <ItemTemplate><asp:Label ID="site-map-static-id" runat="server" /></ItemTemplate>
    </asp:Repeater>
    <asp:Repeater ID="siteMapSecond" runat="server">
      <ItemTemplate><asp:TextBox ID="site-map-static-id" runat="server" /></ItemTemplate>
    </asp:Repeater>
  </NodeTemplate>
</asp:SiteMapPath>
