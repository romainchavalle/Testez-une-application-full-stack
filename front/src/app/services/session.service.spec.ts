import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;

  const mockUser: SessionInformation = {
    token: 'abc123',
    type: 'Bearer',
    id: 1,
    username: 'john.doe',
    firstName: 'John',
    lastName: 'Doe',
    admin: true,
  };

  beforeEach(() => {
    service = new SessionService();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should initialize with isLogged = false', () => {
    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should log in correctly', () => {
    service.logIn(mockUser);

    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(mockUser);
  });

  // it('should emit true when logging in', (done) => {
  //   service.$isLogged().subscribe((isLogged) => {
  //     expect(isLogged).toBe(true);
  //     done();
  //   });

  //   service.logIn(mockUser);
  // });

  it('should log out correctly', () => {
    service.logIn(mockUser);
    service.logOut();

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  // it('should emit false when logging out', (done) => {
  //   service.logIn(mockUser);

  //   service.$isLogged().subscribe((isLogged) => {
  //     expect(isLogged).toBe(false);
  //     done();
  //   });

  //   service.logOut();
  // });
});
