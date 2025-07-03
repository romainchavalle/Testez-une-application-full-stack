import { SessionApiService } from './session-api.service';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpClientMock: jest.Mocked<HttpClient>;

  beforeEach(() => {
    httpClientMock = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn()
    } as unknown as jest.Mocked<HttpClient>;

    service = new SessionApiService(httpClientMock);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call GET all()', () => {
    const mockSessions = [{ id: '1', title: 'Test' }];
    httpClientMock.get.mockReturnValue(of(mockSessions));

    service.all().subscribe(result => {
      expect(result).toEqual(mockSessions);
    });

    expect(httpClientMock.get).toHaveBeenCalledWith('api/session');
  });

   it('should call GET session detail', () => {
    const mockResponse = { id: '1', title: 'Detail' };
    httpClientMock.get.mockReturnValue(of(mockResponse));

    service.detail('1').subscribe();

    expect(httpClientMock.get).toHaveBeenCalledWith('api/session/1');
  });

  it('should call DELETE session', () => {
    httpClientMock.delete.mockReturnValue(of(null));

    service.delete('1').subscribe();

    expect(httpClientMock.delete).toHaveBeenCalledWith('api/session/1');
  });

  it('should call POST create session', () => {
    const session = { id: '1', title: 'New' };
    httpClientMock.post.mockReturnValue(of(session));

    service.create(session as any).subscribe();

    expect(httpClientMock.post).toHaveBeenCalledWith('api/session', session);
  });

  it('should call PUT update session', () => {
    const session = { id: '1', title: 'Updated' };
    httpClientMock.put.mockReturnValue(of(session));

    service.update('1', session as any).subscribe();

    expect(httpClientMock.put).toHaveBeenCalledWith('api/session/1', session);
  });

  it('should call POST participate', () => {
    httpClientMock.post.mockReturnValue(of(void 0));

    service.participate('1', 'user1').subscribe();

    expect(httpClientMock.post).toHaveBeenCalledWith('api/session/1/participate/user1', null);
  });

  it('should call DELETE unParticipate', () => {
    httpClientMock.delete.mockReturnValue(of(void 0));

    service.unParticipate('1', 'user1').subscribe();

    expect(httpClientMock.delete).toHaveBeenCalledWith('api/session/1/participate/user1');
  });
});
