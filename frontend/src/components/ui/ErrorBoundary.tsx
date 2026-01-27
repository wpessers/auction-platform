import { Component, type ReactNode } from 'react';

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

export class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    console.error('ErrorBoundary caught an error:', error, errorInfo);
  }

  handleReset = () => {
    this.setState({ hasError: false, error: null });
  };

  render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback;
      }

      return (
        <div className="flex min-h-[60vh] flex-col items-center justify-center px-4">
          <div className="w-full max-w-md rounded-lg border border-border bg-surface p-8 text-center">
            <div className="mb-4 text-4xl">⚠️</div>
            <h2 className="mb-2 text-xl font-bold text-text-primary">
              Something went wrong
            </h2>
            <p className="mb-6 text-text-secondary">
              An unexpected error occurred. Please try again.
            </p>
            {this.state.error && (
              <details className="mb-6 text-left">
                <summary className="cursor-pointer text-sm text-text-disabled hover:text-text-secondary">
                  Error details
                </summary>
                <pre className="mt-2 overflow-auto rounded bg-background p-3 text-xs text-error">
                  {this.state.error.message}
                </pre>
              </details>
            )}
            <div className="flex flex-col gap-3 sm:flex-row sm:justify-center">
              <button
                onClick={this.handleReset}
                className="rounded bg-accent px-4 py-2 font-medium text-background transition-colors duration-fast hover:bg-accent-hover"
              >
                Try Again
              </button>
              <button
                onClick={() => (window.location.href = '/')}
                className="rounded border border-border bg-card px-4 py-2 font-medium text-text-primary transition-colors duration-fast hover:bg-border"
              >
                Go Home
              </button>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
