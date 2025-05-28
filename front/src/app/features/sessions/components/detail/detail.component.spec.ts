import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule, } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from '../../../../services/session.service';

import { DetailComponent } from './detail.component';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from '../../../../services/teacher.service';
import { MatCardModule, MatCardTitle } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';


describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let service: SessionService;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  }

  // Permet de simuler une session à utiliser à la place de SessionService
  const mockSession = {
    id: 1,
    name: 'Morning Yoga',
    description: 'A great start to your day',
    date: new Date(),
    createdAt: new Date(),
    updatedAt: new Date(),
    users: [1, 2],
    teacher_id: 5
  };

  const mockTeacher = {
    id: 5,
    firstName: 'Alice',
    lastName: 'Smith'
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        MatCardModule,
        MatIconModule
      ],
      declarations: [DetailComponent],
      providers: [{ provide: SessionService, useValue: mockSessionService},
        {provide: SessionApiService, useValue: { detail: () => of(mockSession)}},
        { provide: TeacherService, useValue: { detail: () => of(mockTeacher)}}
      ],
    })
      .compileComponents();
      service = TestBed.inject(SessionService);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display session information', () => {

    const nameEl = fixture.debugElement.query(By.css('h1')).nativeElement;
    expect(nameEl.textContent).toContain(mockSession.name);

    const descriptionEl = fixture.debugElement.query(By.css('.description')).nativeElement;
    expect(descriptionEl.textContent).toContain(mockSession.description);
  });


  it('should display the delete button for admin user', () => {

    const deleteBtn = fixture.debugElement.query(By.css('button[color="warn"] mat-icon'));
    expect(deleteBtn.nativeElement.textContent).toContain('delete');

  });

});
