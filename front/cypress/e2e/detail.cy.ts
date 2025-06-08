describe("detail spec", () => {
  it('should display right informations', () => {

    // Log in
    cy.login();
    
    // click sur le detail
    cy.get('[data-testid="detail-button"]').first().click();

    // Vérifier page detail
    cy.url().should('include', '/detail');


    // vérifier éléments displayed

    // Vérifie que le mat-card existe
    cy.get('mat-card').should('exist');

    // Vérifie le titre (nom de la session, en titlecase)
    cy.get('mat-card-title h1').should('exist').and('not.be.empty');

    // Bouton back avec icône arrow_back
    cy.get('button mat-icon').contains('arrow_back').should('exist');

    // Vérifie la présence du sous-titre avec le teacher
    cy.get('mat-card-subtitle').should('exist')

    cy.get('[data-testid="delete-button"]').should('exist')

  })
})
