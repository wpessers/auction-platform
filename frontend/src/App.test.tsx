import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import { RootLayout } from '@/components/layout/RootLayout';

describe('App', () => {
  it('renders the header with Auction Platform branding', () => {
    render(
      <MemoryRouter>
        <RootLayout />
      </MemoryRouter>
    );
    expect(screen.getByText('Auction Platform')).toBeInTheDocument();
  });

  it('renders navigation links', () => {
    render(
      <MemoryRouter>
        <RootLayout />
      </MemoryRouter>
    );
    expect(screen.getByText('Auctions')).toBeInTheDocument();
    expect(screen.getByText('Login')).toBeInTheDocument();
    expect(screen.getByText('Register')).toBeInTheDocument();
  });
});
