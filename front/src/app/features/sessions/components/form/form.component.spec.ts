import {ComponentFixture, TestBed} from '@angular/core/testing';
import {FormComponent} from "./form.component";
import {HttpClientModule} from "@angular/common/http";
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {ReactiveFormsModule} from "@angular/forms";
import {MatSnackBar, MatSnackBarModule} from "@angular/material/snack-bar";
import {MatSelectModule} from "@angular/material/select";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {expect} from "@jest/globals";
import {SessionService} from "../../../../services/session.service";
import {SessionApiService} from "../../services/session-api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {of} from "rxjs";

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

    const sessionServiceMock = {
      sessionInformation: {
        admin: true
      }
    } as Partial<SessionService>;

    const sessionApiServiceMock = {
      create: jest.fn().mockReturnValue(of({})),
      update: jest.fn().mockReturnValue(of({})),
      detail: jest.fn().mockReturnValue(of({}))
    } as Partial<SessionApiService>;

    const matSnackBarMock = {
      open: jest.fn()
    } as Partial<MatSnackBar>;

    const routerMock = {
      navigate: jest.fn(),
      url: ''
    } as Partial<Router>;

    const activatedRouteMock = { snapshot: { paramMap: { get: jest.fn() } } }

  beforeEach(async () => {
    jest.clearAllMocks();

    await TestBed.configureTestingModule({

      imports: [
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        {provide: SessionService, useValue: sessionServiceMock},
        {provide: MatSnackBar, useValue: matSnackBarMock},
        {provide: SessionApiService, useValue: sessionApiServiceMock},
        {provide: Router, useValue: routerMock},
        {provide: ActivatedRoute, useValue: activatedRouteMock}
      ],
      declarations: [FormComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create the form', () => {
    expect(component).toBeTruthy();
  });

  it('should successfully submit the form', () => {
    // Given
    const session = {
      name: 'Session 1',
      description: 'Description 1',
      date: new Date(),
      teacher_id: 1,
    };

    component.sessionForm?.setValue(session);
    component.onUpdate = false;

    // When
    component.submit();

    // Then
    expect(sessionApiServiceMock.create).toHaveBeenCalledTimes(1);
    expect(sessionApiServiceMock.create).toHaveBeenCalledWith(session);

    expect(matSnackBarMock.open).toHaveBeenCalledTimes(1);
    expect(matSnackBarMock.open).toHaveBeenCalledWith('Session created !', 'Close', {duration: 3000});
    expect(routerMock.navigate).toHaveBeenCalledTimes(1);
  });

  it('should update session', () => {
    // Given

    const session = {
      name: 'Session 1',
      description: 'Description 1',
      date: new Date(),
      teacher_id: 1,
    };

    component.sessionForm?.setValue(session);
    component.onUpdate = true;

    // When
    component.ngOnInit();
    component.submit();

    // Then
    expect(sessionApiServiceMock.update).toHaveBeenCalled();

    expect(matSnackBarMock.open).toHaveBeenCalledTimes(1);
    expect(matSnackBarMock.open).toHaveBeenCalledWith('Session updated !', 'Close', {duration: 3000});
    expect(routerMock.navigate).toHaveBeenCalledTimes(1);
  })
});
