import { AuthService } from './auth.service';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';

describe('AuthService', () => {
  let service: AuthService;
  let httpClientMock: jest.Mocked<HttpClient>;

  const mockRegisterRequest: RegisterRequest = {
    email: 'test@example.com',
    password: 'password123',
    firstName: 'Test',
    lastName: 'User',
  };

  const mockLoginRequest: LoginRequest = {
    email: 'test@example.com',
    password: 'password123',
  };

  const mockSessionInfo: SessionInformation = {
    token: 'abcd1234',
    type: 'Bearer',
    id: 1,
    username: 'testuser',
    firstName: 'Test',
    lastName: 'User',
    admin: false,
  };

  beforeEach(() => {
    httpClientMock = {
      post: jest.fn()
    } as unknown as jest.Mocked<HttpClient>;

    service = new AuthService(httpClientMock);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call register()', () => {
    httpClientMock.post.mockReturnValue(of(void 0));

    service.register(mockRegisterRequest).subscribe((result) => {
      expect(result).toBeUndefined();
    });

    expect(httpClientMock.post).toHaveBeenCalledWith(
      'api/auth/register',
      mockRegisterRequest
    );
  });

  it('should call login()', () => {
    httpClientMock.post.mockReturnValue(of(mockSessionInfo));

    service.login(mockLoginRequest).subscribe((result) => {
      expect(result).toEqual(mockSessionInfo);
    });

    expect(httpClientMock.post).toHaveBeenCalledWith(
      'api/auth/login',
      mockLoginRequest
    );
  });
});
