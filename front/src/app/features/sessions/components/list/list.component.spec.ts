import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { ListComponent } from './list.component';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  }

  const mockSessions = [
    { id: 1, name: 'Yoga Morning', date: new Date(), description: 'A morning session.' }
  ];

  const mockSessionApiService = {
    all: () => of(mockSessions)
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [HttpClientModule, MatCardModule, MatIconModule],
      providers: [{ provide: SessionService, useValue: mockSessionService }]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should display the "Create" button for admin users', () => {
    const createButton = fixture.debugElement.query(By.css('button[routerLink="create"]'));
    expect(createButton).toBeTruthy();
  });

});
