describe('form spec create session', () => {
  it('should create session after complete the form', () => {


    // Mock data for select teacher in the form

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

    // get the create button and click on it to go on create form
    cy.get('[data-testid="create-button"]').should('exist');

    cy.get('[data-testid="create-button"]').click();

    cy.url().should('include', '/create');

    // Attendre que la requête mockée soit terminée
    cy.wait('@getTeachers');

    // Complete the form to create session then submit

    // Name
    cy.get('input[formControlName="name"]').type('testname');

    // Date
    cy.get('input[formControlName="date"]').invoke('val', '2025-06-05').trigger('input')

    // Select teacher
    cy.get('mat-select[formControlName="teacher_id"]').click();
    cy.get('mat-option').should('have.length', 2);
    cy.get('mat-option').first().click();

    // Description
    cy.get('textarea[formControlName="description"]').type('testdescription123456879');
    cy.get('button[type="submit"]').click();

    cy.url().should('include', '/sessions');

    cy.contains('testname').should('exist');
  })
})
