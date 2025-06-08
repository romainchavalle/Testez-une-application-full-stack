describe('form spec edit session', () => {
  it('should edit session after complete the form', () => {

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

    // MOCK the teacher data


    const mockTeachers = [
    {
      id: 1,
      firstName: 'Jean',
      lastName: 'Dupont',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z'
    },
    {
      id: 2,
      firstName: 'Marie',
      lastName: 'Curie',
      createdAt: '2024-01-02T00:00:00Z',
      updatedAt: '2024-01-02T00:00:00Z'
    }
  ]

    cy.intercept('GET', '**/api/teacher', {
      statusCode: 200,
      body: mockTeachers,
    }).as('getTeachers');


    // Log in
    cy.login();

    // Wait for the intercepted request
    cy.wait('@getSessions');

    // click sur le detail
    cy.get('[data-testid="edit-button"]').first().click();

    // Vérifier page detail
    cy.url().should('include', '/update');


    // Attendre que la requête mockée soit terminée
    cy.wait('@getTeachers');

    // modifier valeur du name
    cy.get('input[formControlName="name"]').clear().type('testnamemodified');

    cy.get('button[type="submit"]').click();

    cy.url().should('include', '/sessions');

    cy.contains('testnamemodified').should('exist');

  });
});
