import { useToast, type ToastType } from '@/context/ToastContext';

export function ToastContainer() {
  const { toasts, removeToast } = useToast();

  if (toasts.length === 0) return null;

  return (
    <div className="fixed bottom-4 right-4 z-50 flex flex-col gap-2">
      {toasts.map((toast) => (
        <ToastItem
          key={toast.id}
          type={toast.type}
          message={toast.message}
          action={toast.action}
          onDismiss={() => removeToast(toast.id)}
        />
      ))}
    </div>
  );
}

interface ToastItemProps {
  type: ToastType;
  message: string;
  action?: {
    label: string;
    onClick: () => void;
  };
  onDismiss: () => void;
}

function ToastItem({ type, message, action, onDismiss }: ToastItemProps) {
  const borderColor = {
    success: 'border-l-accent',
    error: 'border-l-error',
    warning: 'border-l-warning',
    info: 'border-l-info',
  }[type];

  const icon = {
    success: (
      <svg
        className="h-5 w-5 text-accent"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2}
          d="M5 13l4 4L19 7"
        />
      </svg>
    ),
    error: (
      <svg
        className="h-5 w-5 text-error"
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
    ),
    warning: (
      <svg
        className="h-5 w-5 text-warning"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2}
          d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
        />
      </svg>
    ),
    info: (
      <svg
        className="h-5 w-5 text-info"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2}
          d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
        />
      </svg>
    ),
  }[type];

  return (
    <div
      className={`flex w-80 items-start gap-3 rounded border-l-4 bg-card p-4 shadow-lg ${borderColor}`}
      role="alert"
    >
      <div className="flex-shrink-0">{icon}</div>
      <div className="flex-1">
        <p className="text-sm text-text-primary">{message}</p>
        {action && (
          <button
            onClick={() => {
              action.onClick();
              onDismiss();
            }}
            className="mt-2 text-sm text-accent transition-colors hover:text-accent-hover"
          >
            {action.label}
          </button>
        )}
      </div>
      <button
        onClick={onDismiss}
        className="flex-shrink-0 text-text-disabled transition-colors hover:text-text-secondary"
        aria-label="Dismiss"
      >
        <svg
          className="h-4 w-4"
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
      </button>
    </div>
  );
}
