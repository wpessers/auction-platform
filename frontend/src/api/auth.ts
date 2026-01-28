import { api } from './client';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface UserProfile {
  userId: string;
  username: string;
  email: string;
}

export const authApi = {
  login: (data: LoginRequest): Promise<AuthTokens> =>
    api.post('/api/auth/login', data),

  register: (data: RegisterRequest): Promise<AuthTokens> =>
    api.post('/api/auth/register', data),

  refresh: (refreshToken: string): Promise<AuthTokens> =>
    api.post('/api/auth/refresh', { refreshToken }),

  getProfile: (): Promise<UserProfile> => api.get('/api/users/me'),
};
