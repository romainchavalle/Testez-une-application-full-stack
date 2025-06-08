describe("list spec", () => {
  it("should display sessions", () => {

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

    // Intercept the GET request to /api/session
    cy.intercept('GET', '**/api/session', {
      statusCode: 200,
      body: mockSessions
    }).as('getSessions');

    // Log in
    cy.login();

    // // Wait for the intercepted request
    cy.wait('@getSessions');

    // Verify the sessions are displayed
    cy.get('.item').should('have.length', mockSessions.length);
  });
});
