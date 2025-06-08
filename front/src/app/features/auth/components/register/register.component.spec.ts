import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';
import {Router} from "@angular/router";
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  const authServiceMock: Partial<AuthService> = {
    register: jest.fn()
  }

  const routerMock: Partial<Router> = {
    navigate: jest.fn()
  }

  beforeEach(async () => {
    // Reset all mocks so that we can verify the number of calls to the methods
    jest.resetAllMocks();

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [
        {provide: AuthService, useValue: authServiceMock},
        {provide: Router, useValue: routerMock}
      ],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });


  it('should register successfully', () => {
    // Given
    jest.spyOn(authServiceMock, 'register').mockReturnValue(of(void 0));

    const credentials = {
      email: 'test@example.com',
      firstName: 'testfirstName',
      lastName: 'testlastName',
      password: 'testmdp'
    }

    component.form.setValue(credentials);

    // When
    component.submit();

    // Then
    expect(authServiceMock.register).toHaveBeenCalledTimes(1);
    expect(authServiceMock.register).toHaveBeenCalledWith(credentials);

    expect(routerMock.navigate).toHaveBeenCalledTimes(1);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);

  });

  it('should not register without credentials', () => {
    // Given
    jest.spyOn(authServiceMock, 'register').mockReturnValue(throwError(() => new Error('Credentials missing')));

    const credentials = {
      email: '',
      firstName: '',
      lastName: '',
      password: ''
    }

    component.form.setValue(credentials);

    // When
    component.submit();

    // Then
    expect(authServiceMock.register).toHaveBeenCalledTimes(1);
    expect(authServiceMock.register).toHaveBeenCalledWith(credentials);

    expect(routerMock.navigate).not.toHaveBeenCalled();
    expect(component.onError).toBe(true);
  });

  it('should mark form as invalid if required fields are missing', () => {
    // Given : tous les champs sont vides
    const emptyForm = {
      email: '',
      firstName: '',
      lastName: '',
      password: ''
    };

    component.form.setValue(emptyForm);

    // When
    const isValid = component.form.valid;

    // Then
    expect(isValid).toBe(false); // le formulaire est invalide
    expect(component.form.get('email')?.hasError('required')).toBe(true);
    expect(component.form.get('firstName')?.hasError('required')).toBe(true);
    expect(component.form.get('lastName')?.hasError('required')).toBe(true);
    expect(component.form.get('password')?.hasError('required')).toBe(true);

    // Pas de tentative d'appel du service
    expect(authServiceMock.register).not.toHaveBeenCalled();
  });
});
