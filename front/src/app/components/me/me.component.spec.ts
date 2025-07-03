import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { SessionService } from 'src/app/services/session.service';

import { MeComponent } from './me.component';
import { of } from 'rxjs';
import { UserService } from 'src/app/services/user.service';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    },
    logOut: jest.fn()
  }

  const mockUser = {
    id: 1,
    email: 'test@example.com',
    firstName: 'John',
    lastName: 'Doe',
    admin: true,
    password: '',
    createdAt: 'January 1, 2020',
    updatedAt: 'January 1, 2021'
  };

  const mockUserService = {
    getById: jest.fn().mockReturnValue(of(mockUser)),
    delete: jest.fn().mockReturnValue(of({})),
  }

  const routerMock = {
    navigate: jest.fn(),
    url: ''
  }

  const mockSnackBar = {
    open: jest.fn()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService},
        { provide: UserService, useValue: mockUserService},
        { provide: Router, useValue: routerMock },
        { provide: MatSnackBar, useValue: mockSnackBar },
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });


  it("should display user's informations", () => {


    // Vérifier que les éléments affichés correspondent bien à la donnée mockée.

    const nameEl = fixture.debugElement.query(By.css('p:nth-of-type(1)')).nativeElement;
    const emailEl = fixture.debugElement.query(By.css('p:nth-of-type(2)')).nativeElement;
    const adminEl = fixture.debugElement.query(By.css('p.my2'));
    const createdAtEl = fixture.debugElement.queryAll(By.css('.p2 p'))[0].nativeElement;
    const updatedAtEl = fixture.debugElement.queryAll(By.css('.p2 p'))[1].nativeElement;

    expect(nameEl.textContent).toContain(mockUser.firstName);
    expect(nameEl.textContent).toContain(mockUser.lastName.toUpperCase());
    expect(emailEl.textContent).toContain(mockUser.email);

    expect(adminEl.nativeElement.textContent).toContain("You are admin");

    expect(createdAtEl.textContent).toContain(mockUser.createdAt);
    expect(updatedAtEl.textContent).toContain(mockUser.updatedAt);

  });

  it('should call delete, show a snackbar, logout, and navigate to home', () => {
    // When
    component.delete();

    // Then
    // Vérifie que userService.delete a été appelé avec l'ID utilisateur
    expect(mockUserService.delete).toHaveBeenCalledTimes(1);

    // Vérifie que le snackbar a été ouvert avec le bon message
    expect(mockSnackBar.open).toHaveBeenCalledTimes(1);

    // Vérifie que la session a été déconnectée
    expect(mockSessionService.logOut).toHaveBeenCalled();

    // Vérifie que la navigation a été redirigée vers la page d'accueil
    expect(routerMock.navigate).toHaveBeenCalledWith(['/']);
  });

});
