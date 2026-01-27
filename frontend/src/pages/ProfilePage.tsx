import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';

export function ProfilePage() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

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
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-text-secondary">
              Username
            </label>
            <p className="mt-1 text-lg text-text-primary">{user.username}</p>
          </div>

          <div>
            <label className="block text-sm font-medium text-text-secondary">
              User ID
            </label>
            <p className="mt-1 font-mono text-sm text-text-secondary">
              {user.userId}
            </p>
          </div>

          <div className="border-t border-border pt-4">
            <p className="text-sm text-text-disabled">
              Note: Email is not available in the JWT token.
            </p>
          </div>
        </div>

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
