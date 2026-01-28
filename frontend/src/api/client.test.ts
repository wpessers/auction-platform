import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { api, ApiError } from './client';

// Mock the env module
vi.mock('@/config/env', () => ({
  env: {
    apiBaseUrl: 'http://localhost:8080/api',
  },
}));

describe('API Client', () => {
  const mockFetch = vi.fn();
  const originalFetch = global.fetch;
  const originalLocation = window.location;

  beforeEach(() => {
    vi.clearAllMocks();
    global.fetch = mockFetch;
    // Reset localStorage mock
    (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue(null);
    (localStorage.removeItem as ReturnType<typeof vi.fn>).mockClear();

    // Mock window.location
    delete (window as { location?: Location }).location;
    window.location = { ...originalLocation, href: '' } as Location;
  });

  afterEach(() => {
    global.fetch = originalFetch;
    window.location = originalLocation;
  });

  describe('GET requests', () => {
    it('makes GET request to correct endpoint', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        text: () => Promise.resolve(JSON.stringify({ data: 'test' })),
      });

      const result = await api.get<{ data: string }>('/test');

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/test',
        expect.objectContaining({
          headers: expect.objectContaining({
            'Content-Type': 'application/json',
          }),
        })
      );
      expect(result).toEqual({ data: 'test' });
    });

    it('includes Authorization header when token exists', async () => {
      (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue('test-token');
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        text: () => Promise.resolve(JSON.stringify({})),
      });

      await api.get('/test');

      expect(mockFetch).toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({
          headers: expect.objectContaining({
            Authorization: 'Bearer test-token',
          }),
        })
      );
    });

    it('does not include Authorization header when no token', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        text: () => Promise.resolve(JSON.stringify({})),
      });

      await api.get('/test');

      const calledHeaders = mockFetch.mock.calls[0][1].headers;
      expect(calledHeaders.Authorization).toBeUndefined();
    });
  });

  describe('POST requests', () => {
    it('makes POST request with JSON body', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 201,
        text: () => Promise.resolve(JSON.stringify({ id: '123' })),
      });

      const body = { name: 'Test', value: 42 };
      const result = await api.post<{ id: string }>('/items', body);

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/items',
        expect.objectContaining({
          method: 'POST',
          body: JSON.stringify(body),
          headers: expect.objectContaining({
            'Content-Type': 'application/json',
          }),
        })
      );
      expect(result).toEqual({ id: '123' });
    });
  });

  describe('PUT requests', () => {
    it('makes PUT request with JSON body', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        text: () => Promise.resolve(JSON.stringify({ updated: true })),
      });

      const body = { name: 'Updated' };
      const result = await api.put<{ updated: boolean }>('/items/123', body);

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/items/123',
        expect.objectContaining({
          method: 'PUT',
          body: JSON.stringify(body),
        })
      );
      expect(result).toEqual({ updated: true });
    });
  });

  describe('DELETE requests', () => {
    it('makes DELETE request', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 204,
        text: () => Promise.resolve(''),
      });

      const result = await api.delete('/items/123');

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/items/123',
        expect.objectContaining({
          method: 'DELETE',
        })
      );
      expect(result).toBeNull();
    });
  });

  describe('Error handling', () => {
    it('throws ApiError on non-ok response', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 400,
        statusText: 'Bad Request',
        text: () => Promise.resolve('Validation failed'),
      });

      try {
        await api.get('/test');
        expect.fail('Expected ApiError to be thrown');
      } catch (error) {
        expect(error).toBeInstanceOf(ApiError);
        expect((error as ApiError).status).toBe(400);
        expect((error as ApiError).message).toBe('Validation failed');
      }
    });

    it('uses statusText when error body is empty', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 500,
        statusText: 'Internal Server Error',
        text: () => Promise.resolve(''),
      });

      await expect(api.get('/test')).rejects.toMatchObject({
        status: 500,
        message: 'Internal Server Error',
      });
    });

    describe('401 Unauthorized handling', () => {
      it('removes token from localStorage on 401', async () => {
        (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue('old-token');
        mockFetch.mockResolvedValueOnce({
          ok: false,
          status: 401,
          statusText: 'Unauthorized',
          text: () => Promise.resolve(''),
        });

        await expect(api.get('/protected')).rejects.toThrow(ApiError);
        expect(localStorage.removeItem).toHaveBeenCalledWith('token');
      });

      it('redirects to login on 401', async () => {
        mockFetch.mockResolvedValueOnce({
          ok: false,
          status: 401,
          statusText: 'Unauthorized',
          text: () => Promise.resolve(''),
        });

        await expect(api.get('/protected')).rejects.toThrow(ApiError);
        expect(window.location.href).toBe('/login');
      });

      it('throws ApiError with 401 status', async () => {
        mockFetch.mockResolvedValueOnce({
          ok: false,
          status: 401,
          statusText: 'Unauthorized',
          text: () => Promise.resolve(''),
        });

        await expect(api.get('/protected')).rejects.toMatchObject({
          status: 401,
          message: 'Unauthorized',
        });
      });
    });
  });

  describe('Response parsing', () => {
    it('handles empty response body', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 204,
        text: () => Promise.resolve(''),
      });

      const result = await api.get('/no-content');
      expect(result).toBeNull();
    });

    it('parses JSON response', async () => {
      const responseData = { items: [1, 2, 3], total: 3 };
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        text: () => Promise.resolve(JSON.stringify(responseData)),
      });

      const result = await api.get<typeof responseData>('/data');
      expect(result).toEqual(responseData);
    });
  });
});

describe('ApiError', () => {
  it('has correct name and properties', () => {
    const error = new ApiError(404, 'Not Found');

    expect(error.name).toBe('ApiError');
    expect(error.status).toBe(404);
    expect(error.message).toBe('Not Found');
    expect(error instanceof Error).toBe(true);
  });
});
