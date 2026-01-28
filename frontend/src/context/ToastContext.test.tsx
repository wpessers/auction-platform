import { render, screen, act, waitFor } from '@testing-library/react';
import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest';
import { ToastProvider, useToast, type ToastType } from './ToastContext';

// Helper component to test the hook
function TestComponent() {
  const { toasts, addToast, removeToast } = useToast();
  return (
    <div>
      <span data-testid="toastCount">{toasts.length}</span>
      <ul data-testid="toastList">
        {toasts.map((toast) => (
          <li key={toast.id} data-testid={`toast-${toast.id}`}>
            {toast.type}: {toast.message}
          </li>
        ))}
      </ul>
      <button
        onClick={() => addToast({ type: 'success', message: 'Success toast' })}
        data-testid="addSuccessBtn"
      >
        Add Success
      </button>
      <button
        onClick={() => addToast({ type: 'error', message: 'Error toast' })}
        data-testid="addErrorBtn"
      >
        Add Error
      </button>
      <button
        onClick={() => addToast({ type: 'warning', message: 'Warning toast' })}
        data-testid="addWarningBtn"
      >
        Add Warning
      </button>
      <button
        onClick={() => addToast({ type: 'info', message: 'Info toast' })}
        data-testid="addInfoBtn"
      >
        Add Info
      </button>
      <button
        onClick={() => {
          if (toasts.length > 0) {
            removeToast(toasts[0].id);
          }
        }}
        data-testid="removeFirstBtn"
      >
        Remove First
      </button>
    </div>
  );
}

describe('ToastContext', () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  describe('Initial state', () => {
    it('starts with empty toast list', () => {
      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      expect(screen.getByTestId('toastCount').textContent).toBe('0');
    });
  });

  describe('addToast', () => {
    it('adds a success toast', () => {
      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      act(() => {
        screen.getByTestId('addSuccessBtn').click();
      });

      expect(screen.getByTestId('toastCount').textContent).toBe('1');
      expect(screen.getByTestId('toastList').textContent).toContain('success: Success toast');
    });

    it('adds an error toast', () => {
      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      act(() => {
        screen.getByTestId('addErrorBtn').click();
      });

      expect(screen.getByTestId('toastCount').textContent).toBe('1');
      expect(screen.getByTestId('toastList').textContent).toContain('error: Error toast');
    });

    it('adds a warning toast', () => {
      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      act(() => {
        screen.getByTestId('addWarningBtn').click();
      });

      expect(screen.getByTestId('toastCount').textContent).toBe('1');
      expect(screen.getByTestId('toastList').textContent).toContain('warning: Warning toast');
    });

    it('adds an info toast', () => {
      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      act(() => {
        screen.getByTestId('addInfoBtn').click();
      });

      expect(screen.getByTestId('toastCount').textContent).toBe('1');
      expect(screen.getByTestId('toastList').textContent).toContain('info: Info toast');
    });

    it('supports multiple toasts stacking', () => {
      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      act(() => {
        screen.getByTestId('addSuccessBtn').click();
        screen.getByTestId('addErrorBtn').click();
        screen.getByTestId('addWarningBtn').click();
      });

      expect(screen.getByTestId('toastCount').textContent).toBe('3');
    });

    it('generates unique IDs for each toast', () => {
      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      act(() => {
        screen.getByTestId('addSuccessBtn').click();
        screen.getByTestId('addSuccessBtn').click();
      });

      const toastList = screen.getByTestId('toastList');
      const toastItems = toastList.querySelectorAll('li');

      expect(toastItems.length).toBe(2);
      const ids = Array.from(toastItems).map((item) => item.getAttribute('data-testid'));
      expect(ids[0]).not.toBe(ids[1]);
    });
  });

  describe('removeToast', () => {
    it('removes a toast by ID', () => {
      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      act(() => {
        screen.getByTestId('addSuccessBtn').click();
      });

      expect(screen.getByTestId('toastCount').textContent).toBe('1');

      act(() => {
        screen.getByTestId('removeFirstBtn').click();
      });

      expect(screen.getByTestId('toastCount').textContent).toBe('0');
    });

    it('only removes the specified toast when multiple exist', () => {
      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      act(() => {
        screen.getByTestId('addSuccessBtn').click();
        screen.getByTestId('addErrorBtn').click();
      });

      expect(screen.getByTestId('toastCount').textContent).toBe('2');

      act(() => {
        screen.getByTestId('removeFirstBtn').click();
      });

      expect(screen.getByTestId('toastCount').textContent).toBe('1');
      expect(screen.getByTestId('toastList').textContent).toContain('error: Error toast');
    });
  });

  describe('Auto-dismiss', () => {
    it('auto-dismisses toast after 5 seconds', async () => {
      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      act(() => {
        screen.getByTestId('addSuccessBtn').click();
      });

      expect(screen.getByTestId('toastCount').textContent).toBe('1');

      // Advance time by 4.9 seconds - toast should still be there
      act(() => {
        vi.advanceTimersByTime(4900);
      });
      expect(screen.getByTestId('toastCount').textContent).toBe('1');

      // Advance time by 0.1 more seconds (total 5 seconds) - toast should be gone
      act(() => {
        vi.advanceTimersByTime(100);
      });
      expect(screen.getByTestId('toastCount').textContent).toBe('0');
    });

    it('auto-dismisses each toast independently', async () => {
      render(
        <ToastProvider>
          <TestComponent />
        </ToastProvider>
      );

      // Add first toast
      act(() => {
        screen.getByTestId('addSuccessBtn').click();
      });

      // Wait 2 seconds, then add second toast
      act(() => {
        vi.advanceTimersByTime(2000);
      });
      act(() => {
        screen.getByTestId('addErrorBtn').click();
      });

      expect(screen.getByTestId('toastCount').textContent).toBe('2');

      // After 3 more seconds (5 total from first), first should be gone
      act(() => {
        vi.advanceTimersByTime(3000);
      });
      expect(screen.getByTestId('toastCount').textContent).toBe('1');
      expect(screen.getByTestId('toastList').textContent).toContain('error: Error toast');

      // After 2 more seconds (5 total from second), second should be gone
      act(() => {
        vi.advanceTimersByTime(2000);
      });
      expect(screen.getByTestId('toastCount').textContent).toBe('0');
    });
  });

  describe('Toast with onClick', () => {
    it('supports onClick handler', () => {
      const onClickMock = vi.fn();

      function ClickableToastTest() {
        const { toasts, addToast } = useToast();
        return (
          <div>
            <button
              onClick={() => addToast({ type: 'info', message: 'Clickable', onClick: onClickMock })}
              data-testid="addClickableBtn"
            >
              Add Clickable
            </button>
            {toasts.map((toast) => (
              <button
                key={toast.id}
                onClick={toast.onClick}
                data-testid="clickableToast"
              >
                {toast.message}
              </button>
            ))}
          </div>
        );
      }

      render(
        <ToastProvider>
          <ClickableToastTest />
        </ToastProvider>
      );

      act(() => {
        screen.getByTestId('addClickableBtn').click();
      });

      act(() => {
        screen.getByTestId('clickableToast').click();
      });

      expect(onClickMock).toHaveBeenCalledTimes(1);
    });
  });

  describe('Toast with action', () => {
    it('supports action button', () => {
      const actionMock = vi.fn();

      function ActionToastTest() {
        const { toasts, addToast } = useToast();
        return (
          <div>
            <button
              onClick={() =>
                addToast({
                  type: 'info',
                  message: 'With action',
                  action: { label: 'Undo', onClick: actionMock },
                })
              }
              data-testid="addActionBtn"
            >
              Add With Action
            </button>
            {toasts.map((toast) => (
              <button
                key={toast.id}
                onClick={toast.action?.onClick}
                data-testid="actionBtn"
              >
                {toast.action?.label}
              </button>
            ))}
          </div>
        );
      }

      render(
        <ToastProvider>
          <ActionToastTest />
        </ToastProvider>
      );

      act(() => {
        screen.getByTestId('addActionBtn').click();
      });

      act(() => {
        screen.getByTestId('actionBtn').click();
      });

      expect(actionMock).toHaveBeenCalledTimes(1);
    });
  });

  describe('useToast hook', () => {
    it('throws error when used outside ToastProvider', () => {
      // Suppress console.error for this test
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

      expect(() => {
        render(<TestComponent />);
      }).toThrow('useToast must be used within a ToastProvider');

      consoleSpy.mockRestore();
    });
  });
});
