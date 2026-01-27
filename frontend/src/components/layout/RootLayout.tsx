import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';

export function RootLayout() {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, isAuthenticated, isLoading, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  // Show loading state while checking auth
  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background">
        <div className="text-text-secondary">Loading...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="border-b border-border bg-surface">
        <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4">
          <Link to="/" className="text-xl font-bold text-accent">
            Auction Platform
          </Link>

          <nav className="flex items-center gap-4">
            <Link
              to="/auctions"
              className={`text-sm transition-colors duration-fast hover:text-accent ${
                location.pathname.startsWith('/auctions')
                  ? 'text-accent'
                  : 'text-text-secondary'
              }`}
            >
              Auctions
            </Link>

            {isAuthenticated ? (
              <>
                <Link
                  to="/profile"
                  className={`text-sm transition-colors duration-fast hover:text-accent ${
                    location.pathname === '/profile'
                      ? 'text-accent'
                      : 'text-text-secondary'
                  }`}
                >
                  {user?.username || 'Profile'}
                </Link>
                <button
                  onClick={handleLogout}
                  className="rounded bg-card px-3 py-1.5 text-sm text-text-secondary transition-colors duration-fast hover:bg-border hover:text-text-primary"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link
                  to="/login"
                  className={`text-sm transition-colors duration-fast hover:text-accent ${
                    location.pathname === '/login'
                      ? 'text-accent'
                      : 'text-text-secondary'
                  }`}
                >
                  Login
                </Link>
                <Link
                  to="/register"
                  className="rounded bg-accent px-3 py-1.5 text-sm font-medium text-background transition-colors duration-fast hover:bg-accent-hover"
                >
                  Register
                </Link>
              </>
            )}
          </nav>
        </div>
      </header>

      {/* Main content */}
      <main className="mx-auto max-w-7xl px-4 py-8">
        <Outlet />
      </main>
    </div>
  );
}
