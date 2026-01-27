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

export interface UserProfile {
  userId: string;
  username: string;
  email: string;
}

export const authApi = {
  login: (data: LoginRequest): Promise<string> =>
    api.post('/api/auth/login', data),

  register: (data: RegisterRequest): Promise<string> =>
    api.post('/api/auth/register', data),

  getProfile: (): Promise<UserProfile> => api.get('/api/users/me'),
};
