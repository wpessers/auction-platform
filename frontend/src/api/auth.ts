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

export const authApi = {
  login: (data: LoginRequest): Promise<string> =>
    api.post('/api/auth/login', data),

  register: (data: RegisterRequest): Promise<void> =>
    api.post('/api/auth/register', data),
};
