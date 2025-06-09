describe("me spec", () => {
  it('should display user informations', () => {
    // Log in
    cy.login();

    // click sur le btn logout
    cy.get('[data-testid="logout-button"]').click();


    // Vérifier page login (pas d'accès à session car pas logué)
    cy.visit('/sessions');
    cy.url().should('include', '/login');

  });
});
