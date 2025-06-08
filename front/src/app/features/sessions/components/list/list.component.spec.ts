import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { ListComponent } from './list.component';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { SessionApiService } from '../../services/session-api.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  }

  // Simule une session
  const mockSessions = [
    { id: 1, name: 'Yoga Morning', date: new Date(), description: 'A morning session.' },
    { id: 2, name: 'Yoga Morning', date: new Date(), description: 'A morning session.' }
  ];

  // Simule le service qui permet de renvoyer les sessions dans le list component
  const mockSessionApiService = {
    all: () => of(mockSessions)
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [HttpClientModule, MatCardModule, MatIconModule, RouterTestingModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should display the "Create" button for admin users', () => {
    // When
    const createButton = fixture.debugElement.query(By.css('button[routerLink="create"]'));

    // Then
    expect(createButton).toBeTruthy();
  });


  it("should display the 'detail' button for each session", () => {
    // When
    const detailButtons = fixture.debugElement.queryAll(By.css('button[data-testid="detail-button"]'));

    // Then
    expect(detailButtons.length).toBe(mockSessions.length);
  });


  it('should display a card for each session', () => {
    // When
    const sessionCards = fixture.debugElement.queryAll(By.css('mat-card.item'));

    // Then
    expect(sessionCards.length).toBe(mockSessions.length);
  });

});
