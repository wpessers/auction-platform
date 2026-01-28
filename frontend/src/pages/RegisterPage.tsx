import { useState, type FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { authApi } from '@/api/auth';
import { ApiError } from '@/api/client';

export function RegisterPage() {
  const navigate = useNavigate();
  const { isAuthenticated, login } = useAuth();

  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Redirect if already authenticated
  if (isAuthenticated) {
    navigate('/auctions', { replace: true });
    return null;
  }

  const validateEmail = (email: string): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);

    // Client-side validation
    if (!username.trim()) {
      setError('Username is required');
      return;
    }
    if (!email.trim()) {
      setError('Email is required');
      return;
    }
    if (!validateEmail(email.trim())) {
      setError('Please enter a valid email address');
      return;
    }
    if (!password) {
      setError('Password is required');
      return;
    }

    setIsSubmitting(true);

    try {
      const tokens = await authApi.register({
        username: username.trim(),
        password,
        email: email.trim(),
      });
      // Auto-login with returned tokens and redirect to auctions
      login(tokens);
      navigate('/auctions', { replace: true });
    } catch (err) {
      if (err instanceof ApiError) {
        // Backend may return "Username already exists" or similar
        setError(err.message || 'Registration failed. Please try again.');
      } else {
        setError('An unexpected error occurred. Please try again.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="flex min-h-[80vh] items-center justify-center">
      <div className="w-full max-w-md rounded-lg bg-surface p-8">
        <h1 className="mb-6 text-2xl font-bold text-text-primary">Register</h1>

        {error && (
          <div className="mb-4 rounded bg-error/10 p-3 text-sm text-error">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label
              htmlFor="username"
              className="mb-1 block text-sm font-medium text-text-secondary"
            >
              Username
            </label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              disabled={isSubmitting}
              autoComplete="username"
              autoFocus
              className="w-full rounded border border-border bg-card px-3 py-2 text-text-primary placeholder-text-disabled outline-none transition-colors duration-fast focus:border-accent focus:ring-1 focus:ring-accent disabled:opacity-50"
              placeholder="Choose a username"
            />
          </div>

          <div>
            <label
              htmlFor="email"
              className="mb-1 block text-sm font-medium text-text-secondary"
            >
              Email
            </label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              disabled={isSubmitting}
              autoComplete="email"
              className="w-full rounded border border-border bg-card px-3 py-2 text-text-primary placeholder-text-disabled outline-none transition-colors duration-fast focus:border-accent focus:ring-1 focus:ring-accent disabled:opacity-50"
              placeholder="Enter your email"
            />
          </div>

          <div>
            <label
              htmlFor="password"
              className="mb-1 block text-sm font-medium text-text-secondary"
            >
              Password
            </label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              disabled={isSubmitting}
              autoComplete="new-password"
              className="w-full rounded border border-border bg-card px-3 py-2 text-text-primary placeholder-text-disabled outline-none transition-colors duration-fast focus:border-accent focus:ring-1 focus:ring-accent disabled:opacity-50"
              placeholder="Choose a password"
            />
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="min-h-[44px] w-full rounded bg-accent py-3 font-medium text-background transition-colors duration-fast hover:bg-accent-hover disabled:cursor-not-allowed disabled:opacity-50"
          >
            {isSubmitting ? 'Creating account...' : 'Create Account'}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-text-secondary">
          Already have an account?{' '}
          <Link
            to="/login"
            className="text-accent transition-colors duration-fast hover:text-accent-hover"
          >
            Login
          </Link>
        </p>
      </div>
    </div>
  );
}
