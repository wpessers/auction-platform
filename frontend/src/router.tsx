import { createBrowserRouter, Navigate } from 'react-router-dom';
import { RootLayout } from '@/components/layout/RootLayout';
import { ProtectedRoute } from '@/components/auth/ProtectedRoute';

import { LoginPage } from '@/pages/LoginPage';
import { RegisterPage } from '@/pages/RegisterPage';
import { AuctionsPage } from '@/pages/AuctionsPage';
import { AuctionDetailPage } from '@/pages/AuctionDetailPage';
import { ProfilePage } from '@/pages/ProfilePage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <RootLayout />,
    children: [
      { path: 'login', element: <LoginPage /> },
      { path: 'register', element: <RegisterPage /> },
      {
        path: 'auctions',
        element: (
          <ProtectedRoute>
            <AuctionsPage />
          </ProtectedRoute>
        ),
      },
      {
        path: 'auctions/:id',
        element: (
          <ProtectedRoute>
            <AuctionDetailPage />
          </ProtectedRoute>
        ),
      },
      {
        path: 'profile',
        element: (
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>
        ),
      },
      { index: true, element: <Navigate to="/auctions" replace /> },
    ],
  },
]);
