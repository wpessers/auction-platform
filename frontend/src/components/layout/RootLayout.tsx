import { Link, Outlet, useLocation } from 'react-router-dom';

export function RootLayout() {
  const location = useLocation();
  const token = localStorage.getItem('token');
  const isAuthenticated = !!token;

  // Parse user info from JWT if available
  const user = isAuthenticated ? parseJwt(token) : null;

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
                  onClick={() => {
                    localStorage.removeItem('token');
                    window.location.href = '/login';
                  }}
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

function parseJwt(token: string): { userId: string; username: string } | null {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const payload = JSON.parse(atob(base64));
    return { userId: payload.userId, username: payload.sub };
  } catch {
    return null;
  }
}
