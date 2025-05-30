import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { SessionService } from 'src/app/services/session.service';

import { MeComponent } from './me.component';
import { of } from 'rxjs';
import { UserService } from 'src/app/services/user.service';
import { By } from '@angular/platform-browser';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
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
  }


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
        { provide: UserService, useValue: mockUserService}
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
    // Test to do

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

});
