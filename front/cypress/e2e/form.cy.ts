describe('form spec create session', () => {
  it('should create session after complete the form', () => {

    // Log in
    cy.visit('/login');
    cy.get('input[formControlName="email"]').type('yoga@studio.com');
    cy.get('input[formControlName="password"]').type('test!1234');
    cy.get('button[type="submit"]').click();
    cy.get('button[type="submit"]').click();

    // Verify login was successful
    cy.url().should('include', '/sessions');


    // get the create button and click on it to go on create form
    cy.get('[data-testid="create-button"]').should('exist');

    cy.get('[data-testid="create-button"]').click();

    cy.location('pathname', { timeout: 10000 }).should('include', '/create');

    // Complete the form then submit

    cy.get('input[formControlName="name"]').type('testname');

    const date = '2025-06-05';
    cy.get('input[formControlName="date"]').invoke('val', date).trigger('input')

    cy.get('mat-select[formControlName="teacher_id"]').click();
    cy.get('mat-option').should('be.visible');
    cy.get('mat-option').first().click();
    cy.get('textarea[formControlName="description"]').type('testdescription123456879');
    cy.get('button[type="submit"]').click();


    cy.location('pathname', { timeout: 10000 }).should('include', '/sessions');

    cy.contains('testname').should('exist');
  })
})
