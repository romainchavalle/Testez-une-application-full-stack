describe("detail spec", () => {
  it('should display right informations', () => {

    // Mock the sessions data
    const mockSessions = [
      {
        id: 1,
        name: 'Yoga du matin',
        date: '2025-06-04T10:00:00Z',
        description: 'Séance de yoga relaxante'
      },
      {
        id: 2,
        name: 'Pilates',
        date: '2025-06-05T17:00:00Z',
        description: 'Travail du centre du corps'
      }
    ];


    cy.intercept('GET', '**/api/session', {
      statusCode: 200,
      body: mockSessions
    }).as('getSessions');


    // Log in
    cy.login();

    // Wait for the intercepted request
    cy.wait('@getSessions');

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
