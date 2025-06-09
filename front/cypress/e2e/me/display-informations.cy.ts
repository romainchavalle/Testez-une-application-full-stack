describe("me spec", () => {
  it('should display user informations', () => {
    // Log in
    cy.login();

    // click sur le btn acoount
    cy.get('[data-testid="account-button"]').click();

    // Vérifier page detail
    cy.url().should('include', '/me');

    cy.contains('yoga@studio.com').should('exist');

  });
});
