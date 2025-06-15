/// <reference types="cypress" />

describe('Login spec', () => {
  it('Login successfull', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.url().should('include', '/sessions')
  })
});


describe('Register spec', () => {
  it('Register successfully', () => {
    cy.visit('/register');

    cy.intercept('POST', '/api/auth/register', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        email: `test@example.com`,
        admin: true
      },
    })

    // remplir champs form
    cy.get('input[formControlName="email"]').type(`test@example.com`);
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


describe("me spec", () => {
  it('should display user informations', () => {
    // Log in
    cy.login();

    cy.intercept('GET', '/api/user/1', {
        body: {
          id: 1,
          username: 'userName',
          firstName: 'firstName',
          lastName: 'lastName',
          email: 'email@email.com',
          admin: true
        },
      });

    // click sur le btn acoount
    cy.get('[data-testid="account-button"]').click();

    // Vérifier page detail
    cy.url().should('include', '/me');

    cy.contains('firstName').should('exist');
    cy.contains('email@email.com').should('exist');

  });
});


describe("me spec", () => {
  it('should logout', () => {
    // Log in
    cy.login();

    // click sur le btn logout
    cy.get('[data-testid="logout-button"]').click();


    // Vérifier page login (pas d'accès à session car pas logué)
    cy.visit('/sessions');
    cy.url().should('include', '/login');

  });
});


describe('form spec create session', () => {
  it('should admin can create a new session', () => {
    // Log in
    cy.login();

    cy.url().should('include', '/sessions');

    cy.intercept('GET', '/api/teacher', {body: [{id: 2, firstName: 'John', lastName: 'Doe'}]});
    cy.intercept('POST', '/api/session', {body: {id: 1}});
    cy.intercept('GET', '/api/session', {body: {id: 1}});

    cy.url().should('include', '/sessions');
    cy.get('button[routerLink=create]').click();
    cy.get('input[formControlName=name]').type('Session 1');
    cy.get('input[formControlName=date]').type('2024-06-17');
    cy.get('mat-select[formControlName=teacher_id]').click().get('mat-option').contains('John Doe').click();
    cy.get('textarea[formControlName=description]').type('Description 1');
    cy.get('button[type=submit]').click();
    cy.contains('Session created !').should('be.visible');
    cy.url().should('include', '/sessions');
  });
})


describe("delete spec", () => {
  it('should delete the session selected', () => {

    // Log in
    cy.login();

    // Intercept get session details
    cy.intercept('GET', '/api/session/1', {
      id: 1,
      name: 'Session 1',
      description: 'Description 1',
      date: '2025-06-04T10:00:00Z',
      teacher_id: 1,
    });


    cy.intercept('DELETE', '**/api/session/*', {
      statusCode: 200,
      body: {},
    }).as('deleteSession');


    // verify first session is present
    cy.contains('Yoga du matin').should('exist');

    // click sur le detail
    cy.get('[data-testid="detail-button"]').first().click();

    // Vérifier page detail
    cy.url().should('include', '/detail');

    // Click on the delete btn
    cy.get('[data-testid="delete-button"]').click();

    // Verify redirect url
    cy.url().should('include', '/sessions');

    // Verify that the session is not present in the DOM anymore
    cy.contains('Session deleted !').should('be.visible');

  });
});

describe("detail spec", () => {
  it('should display right informations', () => {

    // Log in
    cy.login();

    cy.intercept('GET', '/api/session/1', {
      id: 1,
      name: 'Session 1',
      description: 'Description 1',
      date: '2025-06-04T10:00:00Z',
      teacher_id: 1,
    });


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


    // Vérifie la présence du delete btn
    cy.get('[data-testid="delete-button"]').should('exist')

  })
})


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

    // Wait for the intercepted request
    cy.wait('@getSessions');

    // Verify the sessions are displayed
    cy.get('.item').should('have.length', mockSessions.length);

  });

  it("should display create & detail button for admin user", () => {
    // Login
    cy.login();

    // Verify buttons are displayed
    cy.get('[data-testid="create-button"]').should('exist');
    cy.get('[data-testid="detail-button"]').should('exist');
  })
});


describe('form spec edit session', () => {

  it('should admin can update an existing session', () => {
    cy.login();

    cy.intercept('GET', '/api/session/1', {
      id: 1,
      name: 'Session 1',
      description: 'Description 1',
      date: '2024-06-17',
      teacher_id: 1,
    });

    cy.contains('Edit').click();

    cy.intercept('PUT', '/api/session/1', {});
    cy.get('button[type="submit"]').click();

    cy.contains('Session updated !').should('be.visible');
  });



  it('should not submit when missing input on update', ()=> {

    // Log in
    cy.login();

    cy.intercept('GET', '/api/session/1', {
      id: 1,
      name: 'Session 1',
      description: 'Description 1',
      date: '2024-06-17',
      teacher_id: 1,
    });

    // click sur le detail
    cy.get('[data-testid="edit-button"]').first().click();

    // Vérifier page detail
    cy.url().should('include', '/update');

    // clear le name
    cy.get('input[formControlName="name"]').clear()

    // Vérifier le disable
    cy.get('button[type="submit"]').should('be.disabled');

  });
});
