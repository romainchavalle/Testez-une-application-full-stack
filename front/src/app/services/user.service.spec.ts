import { UserService } from './user.service';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { User } from '../interfaces/user.interface';

describe('UserService', () => {
  let service: UserService;
  let httpClientMock: jest.Mocked<HttpClient>;

  const mockUser: User = {
    id: 1,
    email: 'john@example.com',
    lastName: 'Doe',
    firstName: 'John',
    admin: false,
    password: 'hashedpassword',
    createdAt: new Date('2024-01-01T00:00:00Z'),
    updatedAt: new Date('2024-06-01T00:00:00Z'),
  };

  beforeEach(() => {
    httpClientMock = {
      get: jest.fn(),
      delete: jest.fn()
    } as unknown as jest.Mocked<HttpClient>;

    service = new UserService(httpClientMock);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call GET getById()', () => {
    httpClientMock.get.mockReturnValue(of(mockUser));

    service.getById('1').subscribe((result) => {
      expect(result).toEqual(mockUser);
    });

    expect(httpClientMock.get).toHaveBeenCalledWith('api/user/1');
  });

  it('should call DELETE delete()', () => {
    httpClientMock.delete.mockReturnValue(of(null));

    service.delete('1').subscribe((result) => {
      expect(result).toBeNull();
    });

    expect(httpClientMock.delete).toHaveBeenCalledWith('api/user/1');
  });
});
