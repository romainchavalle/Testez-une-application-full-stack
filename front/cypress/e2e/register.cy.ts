describe('Register spec', () => {
  it('Register successfully', () => {
    cy.visit('/register');

    // remplir champs form
    cy.get('input[formControlName="email"]').type(`${Math.random()}@example.com`);
    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="password"]').type('TestPassword123');

    // submit le form
    cy.get('form').submit();

    // Vérif la redirection vers le login
    cy.url().should('include', '/login');

  })

  it('should show error with uncomplete field', () => {
    cy.visit('/register');

    // remplir champs form
    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="password"]').type('TestPassword123');

    // submit le form
    cy.get('form').submit();

    // Reste sur la page
    cy.url().should('include', '/register');

    // Affiche le message d'erreur
    cy.get('[data-cy="error"]').should('be.visible');

  })
})
