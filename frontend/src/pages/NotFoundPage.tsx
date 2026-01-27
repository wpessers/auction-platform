import { Link } from 'react-router-dom';

export function NotFoundPage() {
  return (
    <div className="flex min-h-[60vh] flex-col items-center justify-center px-4">
      <div className="text-center">
        <h1 className="mb-2 text-6xl font-bold text-accent">404</h1>
        <h2 className="mb-4 text-2xl font-semibold text-text-primary">
          Page Not Found
        </h2>
        <p className="mb-8 max-w-md text-text-secondary">
          The page you're looking for doesn't exist or has been moved.
        </p>
        <Link
          to="/auctions"
          className="inline-block rounded bg-accent px-6 py-3 font-medium text-background transition-colors duration-fast hover:bg-accent-hover"
        >
          Back to Auctions
        </Link>
      </div>
    </div>
  );
}
