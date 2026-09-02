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
