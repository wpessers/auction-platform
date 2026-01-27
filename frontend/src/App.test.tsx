import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import { AuthProvider } from '@/context/AuthContext';
import { WebSocketProvider } from '@/context/WebSocketContext';
import { ToastProvider } from '@/context/ToastContext';
import { RootLayout } from '@/components/layout/RootLayout';

function renderWithProviders(ui: React.ReactElement) {
  return render(
    <AuthProvider>
      <WebSocketProvider>
        <ToastProvider>
          <MemoryRouter>{ui}</MemoryRouter>
        </ToastProvider>
      </WebSocketProvider>
    </AuthProvider>
  );
}

describe('App', () => {
  it('renders the header with Auction Platform branding', () => {
    renderWithProviders(<RootLayout />);
    expect(screen.getByText('Auction Platform')).toBeInTheDocument();
  });

  it('renders navigation links', () => {
    renderWithProviders(<RootLayout />);
    expect(screen.getByText('Auctions')).toBeInTheDocument();
    expect(screen.getByText('Login')).toBeInTheDocument();
    expect(screen.getByText('Register')).toBeInTheDocument();
  });
});
