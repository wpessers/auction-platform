import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { authApi, type UserProfile } from '@/api/auth';
import { Spinner } from '@/components/ui/Spinner';

export function ProfilePage() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const data = await authApi.getProfile();
        setProfile(data);
      } catch {
        setError('Failed to load profile');
      } finally {
        setIsLoading(false);
      }
    };

    fetchProfile();
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  if (!user) {
    return null;
  }

  return (
    <div className="mx-auto max-w-2xl">
      <h1 className="mb-6 text-2xl font-bold text-text-primary">Profile</h1>

      <div className="rounded-lg bg-surface p-6">
        {isLoading ? (
          <div className="flex justify-center py-8">
            <Spinner />
          </div>
        ) : error ? (
          <div className="py-4 text-center text-error">{error}</div>
        ) : (
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-text-secondary">
                Username
              </label>
              <p className="mt-1 text-lg text-text-primary">
                {profile?.username ?? user.username}
              </p>
            </div>

            <div>
              <label className="block text-sm font-medium text-text-secondary">
                Email
              </label>
              <p className="mt-1 text-lg text-text-primary">{profile?.email}</p>
            </div>

            <div>
              <label className="block text-sm font-medium text-text-secondary">
                User ID
              </label>
              <p className="mt-1 font-mono text-sm text-text-secondary">
                {profile?.userId ?? user.userId}
              </p>
            </div>
          </div>
        )}

        <div className="mt-8 border-t border-border pt-6">
          <button
            onClick={handleLogout}
            className="rounded bg-error px-4 py-2 font-medium text-white transition-colors duration-fast hover:bg-error/80"
          >
            Logout
          </button>
        </div>
      </div>
    </div>
  );
}
