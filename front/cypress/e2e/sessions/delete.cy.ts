describe("delete spec", () => {
  it('should delete the session selected', () => {
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

    // verify first session is present
    cy.contains('Yoga du matin').should('exist');

    // click sur le detail
    cy.get('[data-testid="detail-button"]').first().click();

    // Vérifier page detail
    cy.url().should('include', '/detail');

    // Click on the delete btn
    cy.get('[data-testid="delete-button"]').click;

    // Verify redirect url
    cy.url().should('include', '/sessions');

    // Verify that the session is not present in the DOM anymore
    cy.contains('Yoga du matin').should('not.exist');

  });
});
