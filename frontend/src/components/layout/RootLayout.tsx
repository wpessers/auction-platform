import { useState, useEffect } from 'react';
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { Spinner } from '@/components/ui/Spinner';

export function RootLayout() {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, isAuthenticated, isLoading, logout } = useAuth();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  // Close mobile menu on route change
  useEffect(() => {
    setIsMobileMenuOpen(false);
  }, [location.pathname]);

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  // Show loading state while checking auth
  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background">
        <Spinner size="lg" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="sticky top-0 z-40 border-b border-border bg-surface">
        <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4">
          <Link to="/" className="text-xl font-bold text-accent">
            Auction Platform
          </Link>

          {/* Mobile menu button */}
          <button
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            className="rounded p-2 text-text-secondary transition-colors hover:bg-card hover:text-text-primary sm:hidden"
            aria-label={isMobileMenuOpen ? 'Close menu' : 'Open menu'}
            aria-expanded={isMobileMenuOpen}
          >
            {isMobileMenuOpen ? (
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-6 w-6"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            ) : (
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-6 w-6"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M4 6h16M4 12h16M4 18h16"
                />
              </svg>
            )}
          </button>

          {/* Desktop navigation */}
          <nav className="hidden items-center gap-4 sm:flex">
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

        {/* Mobile navigation */}
        {isMobileMenuOpen && (
          <nav className="animate-slide-up border-t border-border bg-surface px-4 py-4 sm:hidden">
            <div className="flex flex-col gap-3">
              <Link
                to="/auctions"
                className={`rounded px-3 py-2 text-sm transition-colors ${
                  location.pathname.startsWith('/auctions')
                    ? 'bg-accent-muted text-accent'
                    : 'text-text-secondary hover:bg-card hover:text-text-primary'
                }`}
              >
                Auctions
              </Link>

              {isAuthenticated ? (
                <>
                  <Link
                    to="/profile"
                    className={`rounded px-3 py-2 text-sm transition-colors ${
                      location.pathname === '/profile'
                        ? 'bg-accent-muted text-accent'
                        : 'text-text-secondary hover:bg-card hover:text-text-primary'
                    }`}
                  >
                    {user?.username || 'Profile'}
                  </Link>
                  <button
                    onClick={handleLogout}
                    className="rounded px-3 py-2 text-left text-sm text-text-secondary transition-colors hover:bg-card hover:text-text-primary"
                  >
                    Logout
                  </button>
                </>
              ) : (
                <>
                  <Link
                    to="/login"
                    className={`rounded px-3 py-2 text-sm transition-colors ${
                      location.pathname === '/login'
                        ? 'bg-accent-muted text-accent'
                        : 'text-text-secondary hover:bg-card hover:text-text-primary'
                    }`}
                  >
                    Login
                  </Link>
                  <Link
                    to="/register"
                    className="rounded bg-accent px-3 py-2 text-center text-sm font-medium text-background transition-colors hover:bg-accent-hover"
                  >
                    Register
                  </Link>
                </>
              )}
            </div>
          </nav>
        )}
      </header>

      {/* Main content */}
      <main className="mx-auto max-w-7xl px-4 py-8">
        <Outlet />
      </main>
    </div>
  );
}
