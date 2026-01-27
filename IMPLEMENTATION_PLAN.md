# Auction Platform Implementation Plan

This document outlines the complete implementation roadmap for the Auction Platform, including required backend fixes and prioritized frontend development phases.

**Last Verified:** 2026-01-26

---

## Table of Contents

1. [Current Status Summary](#current-status-summary)
2. [Backend Fixes Required](#backend-fixes-required)
3. [Frontend Implementation Phases](#frontend-implementation-phases)
   - [Phase 1: Foundation (CRITICAL)](#phase-1-foundation-critical)
   - [Phase 2: Authentication (HIGH)](#phase-2-authentication-high)
   - [Phase 3: Auction Browsing (HIGH)](#phase-3-auction-browsing-high)
   - [Phase 4: Real-Time & Bidding (HIGH)](#phase-4-real-time--bidding-high)
   - [Phase 5: Auction Creation (MEDIUM)](#phase-5-auction-creation-medium)
   - [Phase 6: Polish (LOW)](#phase-6-polish-low)
4. [Dependencies Diagram](#dependencies-diagram)
5. [API Reference](#api-reference)
6. [WebSocket Message Formats](#websocket-message-formats)

---

## Current Status Summary

### Overall Progress

| Area | Status | Notes |
|------|--------|-------|
| **Backend Core** | ~90% complete | 5 verified bugs (3 CRITICAL, 2 HIGH) + 1 LOW |
| **Frontend** | Not started | Bare skeleton only |
| **Specs** | 100% complete | All 6 specs comprehensive and ready |
| **Price Service** | Not implemented | Skeleton only - gRPC service placeholder |

### Backend Status (Verified 2026-01-26)

| Component | Status | Blocking Issues |
|-----------|--------|-----------------|
| Authentication | Working | None |
| Auction CRUD | Broken | Routing bug (line 42-50), missing response fields |
| Bidding (WebSocket) | Partially broken | BidRejectedMessage sends wrong ID (line 38-40) |
| Auction Lifecycle | Not implemented | No @Scheduled tasks, no @EnableScheduling |
| Redis Registry | Not implemented | All 3 methods throw UnsupportedOperationException |

### Frontend Status (Verified 2026-01-26)

```
frontend/
├── src/
│   ├── App.tsx          # Only displays "Auction Platform" heading (light bg-gray-100)
│   ├── App.test.tsx     # Single test verifying heading renders
│   ├── main.tsx         # React entry point with StrictMode
│   ├── index.css        # Only @import "tailwindcss" (no theme vars)
│   └── test/setup.ts    # Vitest setup with jest-dom matchers
├── public/
│   └── vite.svg         # Vite logo
├── package.json         # React 19.2.3, Tailwind 4.1.18, Vite 7.2.4, Vitest 4.0.18
├── tsconfig.json        # ES2022, strict mode (NO path aliases)
└── vite.config.ts       # React + Tailwind plugins, Vitest jsdom config
```

**Missing in Frontend:**
- Routing (no react-router-dom)
- State Management (no auth context)
- API Client (no fetch wrapper)
- WebSocket (no @stomp/stompjs, no sockjs-client)
- Date utilities (no date-fns)
- Components (no UI components)
- Configuration (no path aliases in tsconfig, no env vars)
- Dark Theme (using light bg-gray-100, needs #0a0a0a dark theme)

### Specs Available

All specs exist in `/specs/` (NOT `/docs/specs/frontend/`):
- `authentication.md` - Auth flows and JWT handling
- `auction-browsing.md` - List and detail views
- `auction-creation.md` - Create auction form
- `bidding.md` - Bid submission and validation
- `real-time-updates.md` - WebSocket integration
- `ui-design-system.md` - Dark theme, colors, typography

---

## Backend Fixes Required

### Priority: CRITICAL (Blocks Frontend Development)

~~All CRITICAL issues have been resolved.~~

- [x] **Issue 1: AuctionController Routing Bug** - RESOLVED (2026-01-27)
  - Reordered `/active` endpoint before `/{id}` in `AuctionController.java`
- [x] **Issue 2: AuctionResponse Missing Fields** - RESOLVED (2026-01-27)
  - Added `highestBid`, `currentWinnerId`, and `status` fields to `AuctionResponse`
- [x] **Issue 3: AuctionMapper Not Updated** - RESOLVED (2026-01-27)
  - Updated mapper to include the 3 new fields with null handling for `highestBid`

---

### Priority: HIGH (Causes Incorrect Behavior)

~~All HIGH priority backend issues have been resolved.~~

- [x] **Issue 4: BidRejectedMessage Sends Wrong ID** - RESOLVED (2026-01-27)
  - Fixed `SpringBidEventListener.java` to use `event.bidderId()` instead of `event.auctionId()`
- [x] **Issue 5: No Auction Lifecycle Scheduler** - RESOLVED (2026-01-27)
  - Added `@EnableScheduling` to main application class
  - Created `AuctionLifecycleScheduler` component with `@Scheduled` methods
  - Added repository methods for time-based queries
  - Scheduler runs every 30 seconds to activate/close auctions

---

### Priority: LOW (Non-Blocking)

These issues don't affect development workflow.

---

#### Issue 6: RedisAuctionRegistryAdapter Not Implemented

- [ ] Implement `RedisAuctionRegistryAdapter` methods (prod profile only)

**Impact**: None for frontend development. Only active with `@Profile("prod")`.

**Current State**: All methods throw `UnsupportedOperationException`.

**Note**: The `InMemoryAuctionRegistry` is used by default and works correctly. This is only needed for production horizontal scaling.

---

---

### Priority: MEDIUM (Spec Discrepancy)

These issues are inconsistencies between specs and backend behavior.

---

#### Issue 7: Registration Doesn't Auto-Login (Spec Mismatch)

- [ ] **DECISION REQUIRED**: Either update backend to return JWT on register, OR update spec

**Impact**: Spec says "On successful registration, user is automatically logged in" but backend returns empty 201.

**Spec Location**: `/specs/authentication.md` line 14

**Current Backend Behavior**: `POST /api/auth/register` returns empty `201 Created`, no JWT token.

**Options**:
1. **Update Backend**: Modify `UserAuthController.register()` to return JWT token (like login)
2. **Update Spec**: Change spec to say "redirect to login with success message" (current workaround)

---

### Workarounds (No Backend Fix Needed)

#### Registration Doesn't Auto-Login

**Current Behavior**: `POST /api/auth/register` returns empty `201 Created`, no JWT token.

**Workaround**: After successful registration, redirect to login page with success message.

```typescript
async function register(data: RegisterData) {
  await api.post('/api/auth/register', data);
  navigate('/login', { state: { message: 'Registration successful! Please log in.' } });
}
```

---

#### No Profile Endpoint

**Current Behavior**: No `GET /api/profile` endpoint exists.

**Workaround**: Extract user info from JWT token claims on the client side.

```typescript
function parseJwt(token: string): { userId: string; username: string } {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const payload = JSON.parse(atob(base64));
  return { userId: payload.userId, username: payload.sub };
}
```

---

## Frontend Implementation Phases

### Phase 1: Foundation (CRITICAL) - ✅ COMPLETED 2026-01-27

**Priority**: ~~CRITICAL~~ DONE
**Estimated Effort**: 2-3 hours
**Blockers**: Backend Issues 1-3 must be fixed first ✓
**Blocks**: All other phases

This phase establishes the project foundation. All subsequent phases depend on this.

#### 1.1 Install Dependencies

- [x] Install routing: `react-router-dom@7`
- [x] Install WebSocket: `@stomp/stompjs`
- [x] Install WebSocket transport: `sockjs-client` + `@types/sockjs-client`
- [x] Install date utilities: `date-fns`

```bash
cd frontend
npm install react-router-dom@7 @stomp/stompjs sockjs-client date-fns
npm install -D @types/sockjs-client
```

#### 1.2 Configure Path Aliases

- [x] Update `vite.config.ts` with path aliases
- [x] Update `tsconfig.json` with path mappings

**vite.config.ts**:
```typescript
import path from 'path';

export default defineConfig({
  // ...existing config
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
});
```

**tsconfig.json** (add to compilerOptions):
```json
{
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@/*": ["./src/*"]
    }
  }
}
```

#### 1.3 Environment Variables

- [x] Create `.env.development` with local backend URLs
- [x] Create `.env.production` template
- [x] Create `src/config/env.ts` for typed access

**.env.development**:
```
VITE_API_BASE_URL=http://localhost:8080
VITE_WS_URL=ws://localhost:8080/ws
```

**src/config/env.ts**:
```typescript
export const env = {
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL as string,
  wsUrl: import.meta.env.VITE_WS_URL as string,
} as const;
```

#### 1.4 API Client Setup

- [x] Create `src/api/client.ts` - Base fetch wrapper with auth headers
- [x] Create `src/api/auth.ts` - Auth endpoints
- [x] Create `src/api/auctions.ts` - Auction endpoints

**src/api/client.ts**:
```typescript
import { env } from '@/config/env';

class ApiError extends Error {
  constructor(public status: number, message: string) {
    super(message);
  }
}

async function request<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const token = localStorage.getItem('token');

  const response = await fetch(`${env.apiBaseUrl}${endpoint}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options.headers,
    },
  });

  if (response.status === 401) {
    localStorage.removeItem('token');
    window.location.href = '/login';
    throw new ApiError(401, 'Unauthorized');
  }

  if (!response.ok) {
    const error = await response.text();
    throw new ApiError(response.status, error);
  }

  const text = await response.text();
  return text ? JSON.parse(text) : null;
}

export const api = {
  get: <T>(endpoint: string) => request<T>(endpoint),
  post: <T>(endpoint: string, body: unknown) =>
    request<T>(endpoint, { method: 'POST', body: JSON.stringify(body) }),
};
```

#### 1.5 Dark Theme Tailwind Configuration

- [x] Update `src/index.css` with CSS variables for dark theme
- [x] Configure dark mode colors per UI design system spec

**src/index.css**:
```css
@import "tailwindcss";

@theme {
  --color-background: #0a0a0a;
  --color-surface: #171717;
  --color-card: #262626;
  --color-border: #404040;

  --color-text-primary: #fafafa;
  --color-text-secondary: #a3a3a3;
  --color-text-disabled: #525252;

  --color-accent: #22c55e;
  --color-accent-hover: #4ade80;
  --color-accent-muted: #166534;

  --color-error: #ef4444;
  --color-warning: #f59e0b;
  --color-info: #3b82f6;
}
```

#### 1.6 Router Setup

- [x] Create `src/router.tsx` with route definitions
- [x] Create placeholder pages for all routes
- [x] Update `App.tsx` to use router

**src/router.tsx**:
```typescript
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
        element: <ProtectedRoute><AuctionsPage /></ProtectedRoute>
      },
      {
        path: 'auctions/:id',
        element: <ProtectedRoute><AuctionDetailPage /></ProtectedRoute>
      },
      {
        path: 'profile',
        element: <ProtectedRoute><ProfilePage /></ProtectedRoute>
      },
      { index: true, element: <Navigate to="/auctions" replace /> },
    ],
  },
]);
```

#### 1.7 Base Layout Component

- [x] Create `src/components/layout/RootLayout.tsx` with header, main, outlet
- [x] Apply dark theme background
- [x] Add navigation header skeleton

**Folder structure after Phase 1**:
```
src/
├── api/
│   ├── client.ts
│   ├── auth.ts
│   └── auctions.ts
├── components/
│   ├── auth/
│   │   └── ProtectedRoute.tsx
│   └── layout/
│       └── RootLayout.tsx
├── config/
│   └── env.ts
├── pages/
│   ├── LoginPage.tsx
│   ├── RegisterPage.tsx
│   ├── AuctionsPage.tsx
│   ├── AuctionDetailPage.tsx
│   └── ProfilePage.tsx
├── router.tsx
├── App.tsx
├── main.tsx
└── index.css
```

---

### Phase 2: Authentication (HIGH) - ✅ COMPLETED 2026-01-27

**Priority**: ~~HIGH~~ DONE
**Estimated Effort**: 3-4 hours
**Blockers**: Phase 1 complete ✓
**Blocks**: Phases 4, 5 (bidding and creation require auth)

#### 2.1 Auth Context & Provider

- [x] Create `src/context/AuthContext.tsx`
- [x] Implement JWT token storage/retrieval
- [x] Implement JWT parsing for user info
- [x] Add login/logout functions
- [x] Add loading state for initial auth check

**src/context/AuthContext.tsx**:
```typescript
interface AuthContextType {
  user: { userId: string; username: string } | null;
  token: string | null;
  isLoading: boolean;
  login: (token: string) => void;
  logout: () => void;
}

function parseJwt(token: string) {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const payload = JSON.parse(atob(base64));
  return { userId: payload.userId, username: payload.sub };
}
```

#### 2.2 Protected Route Component

- [x] Create `src/components/auth/ProtectedRoute.tsx`
- [x] Redirect to login if not authenticated
- [x] Store intended destination for post-login redirect
- [x] Show loading state during auth check

#### 2.3 Login Page

- [x] Create `src/pages/LoginPage.tsx`
- [x] Username and password inputs
- [x] Form validation (required fields)
- [x] Error message display
- [x] Link to registration page
- [x] Handle success message from registration redirect

**API Contract**:
```typescript
// POST /api/auth/login
// Request
{ "username": string, "password": string }

// Response: 200 OK
"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."  // JWT token as plain text
```

#### 2.4 Register Page

- [x] Create `src/pages/RegisterPage.tsx`
- [x] Username, email, password inputs
- [x] Form validation (required, email format)
- [x] Error message display (e.g., "Username already exists")
- [x] Link to login page
- [x] Redirect to login on success (workaround for no auto-login)

**API Contract**:
```typescript
// POST /api/auth/register
// Request
{ "username": string, "password": string, "email": string }

// Response: 201 Created (empty body)
```

#### 2.5 Profile Page

- [x] Create `src/pages/ProfilePage.tsx`
- [x] Display username from JWT
- [x] Display userId (for debugging/reference)
- [x] Logout button
- [x] Note: Email not available in JWT

#### 2.6 Header with Auth State

- [x] Update `RootLayout` header with conditional rendering
- [x] Show login/register links when logged out
- [x] Show username + profile dropdown when logged in
- [x] Implement logout from dropdown

---

### Phase 3: Auction Browsing (HIGH) - ✅ COMPLETED 2026-01-27

**Priority**: ~~HIGH~~ DONE
**Estimated Effort**: 4-5 hours
**Blockers**: Phase 1 complete, Backend Issues 1-3 fixed
**Blocks**: Phase 4 (detail page needed for bidding)

#### 3.1 Type Definitions

- [x] Create `src/types/auction.ts`

```typescript
export interface Auction {
  id: string;
  name: string;
  description: string;
  startTime: string;      // ISO-8601
  endTime: string;        // ISO-8601
  startingPrice: number;
  highestBid: number | null;
  currentWinnerId: string | null;
  status: 'SCHEDULED' | 'ACTIVE' | 'CLOSED';
}
```

#### 3.2 Auction API Functions

- [x] Create `src/api/auctions.ts`

```typescript
export async function getActiveAuctions(): Promise<Auction[]> {
  return api.get('/api/auctions/active');
}

export async function getAuction(id: string): Promise<Auction> {
  return api.get(`/api/auctions/${id}`);
}
```

#### 3.3 Countdown Timer Hook

- [x] Create `src/hooks/useCountdown.ts`
- [x] Return formatted time remaining
- [x] Update every second when < 5 minutes
- [x] Update every minute otherwise
- [x] Handle expired auctions

```typescript
function useCountdown(endTime: string): {
  timeRemaining: string;
  isUrgent: boolean;    // < 5 minutes
  isExpired: boolean;
}
```

#### 3.4 Auction Card Component

- [x] Create `src/components/auctions/AuctionCard.tsx`
- [x] Display: name, time remaining, current bid (or starting price)
- [x] Visual indicator for active/urgent/ended status
- [x] Click navigates to detail page
- [x] Dark theme styling per UI spec

#### 3.5 Auctions List Page

- [x] Create `src/pages/AuctionsPage.tsx`
- [x] Fetch active auctions on mount
- [x] Display as responsive card grid
- [x] Search input (client-side filtering)
- [x] Empty state for no auctions
- [x] Empty state for no search results
- [x] Loading state

#### 3.6 Auction Detail Page

- [x] Create `src/pages/AuctionDetailPage.tsx`
- [x] Fetch auction by ID
- [x] Display all auction fields
- [x] Live countdown timer
- [x] Current bid display
- [x] Back navigation to list
- [x] Placeholder for bidding panel (Phase 4)

---

### Phase 4: Real-Time & Bidding (HIGH) - ✅ COMPLETED 2026-01-27

**Priority**: ~~HIGH~~ DONE
**Estimated Effort**: 5-6 hours
**Blockers**: Phases 2 & 3 complete, Backend Issue 4 fixed
**Blocks**: Phase 6 (polish depends on core features)

#### 4.1 WebSocket Service

- [x] Create `src/services/websocket.ts`
- [x] STOMP client setup with SockJS fallback
- [x] Connect with JWT token in headers
- [x] Auto-reconnect with exponential backoff
- [x] Connection state management

```typescript
class WebSocketService {
  connect(token: string): void;
  disconnect(): void;
  subscribe(destination: string, callback: (message: any) => void): () => void;
  send(destination: string, body: object): void;
  getConnectionState(): 'connecting' | 'connected' | 'disconnected';
}
```

#### 4.2 WebSocket Context

- [x] Create `src/context/WebSocketContext.tsx`
- [x] Connect when authenticated
- [x] Disconnect on logout
- [x] Expose connection state
- [x] Provide subscription helpers

#### 4.3 Connection Status Indicator

- [x] Create `src/components/ui/ConnectionStatus.tsx`
- [x] Show only when disconnected > 5 seconds
- [x] Subtle, non-intrusive design

#### 4.4 Toast Notification System

- [x] Create `src/components/ui/Toast.tsx`
- [x] Create `src/context/ToastContext.tsx`
- [x] Support success/error/warning/info types
- [x] Auto-dismiss after 5 seconds
- [x] Clickable toasts (for outbid navigation)
- [x] Stack multiple toasts

#### 4.5 Real-Time Auction Updates

- [x] Subscribe to `/topic/auctions/{id}` on detail page
- [x] Update auction state when bid received
- [x] Unsubscribe on page leave

#### 4.6 Outbid Notifications

- [x] Subscribe to `/user/queue/notifications`
- [x] Show toast when outbid
- [x] Toast links to auction

#### 4.7 Bidding Panel Component

- [x] Create `src/components/bidding/BiddingPanel.tsx`
- [x] Show current highest bid
- [x] Quick bid buttons (+$5, +$10, +$50)
- [x] Custom amount input
- [x] Client-side validation
- [x] Loading state during submission
- [x] Disable when auction not active

#### 4.8 Bid Submission

- [x] Send bid via WebSocket to `/app/bid`
- [x] Handle success (update UI)
- [x] Handle BID_TOO_LOW error
- [x] Handle AUCTION_CLOSED error
- [x] Subscribe to `/user/queue/errors` for rejections

#### 4.9 Auth-Gated Bidding

- [x] Show login prompt for unauthenticated users
- [x] Show bidding panel only when authenticated

---

### Phase 5: Auction Creation (MEDIUM)

**Priority**: MEDIUM
**Estimated Effort**: 3-4 hours
**Blockers**: Phases 2 & 3 complete
**Blocks**: None (independent feature)

#### 5.1 Floating Action Button

- [ ] Create `src/components/ui/FAB.tsx`
- [ ] Position bottom-right, green accent
- [ ] Only visible to authenticated users
- [ ] Plus icon
- [ ] Click opens creation modal

#### 5.2 Create Auction Modal

- [ ] Create `src/components/auctions/CreateAuctionModal.tsx`
- [ ] Modal overlay with backdrop blur
- [ ] Close on backdrop click or X button
- [ ] Keyboard accessible (Escape to close)

#### 5.3 Auction Form

- [ ] Name input (required)
- [ ] Description textarea (required)
- [ ] Start time datetime picker (required, >= now)
- [ ] End time datetime picker (required, > start time)
- [ ] Starting price number input (required, >= 0)
- [ ] "Start now" checkbox option
- [ ] Inline validation errors

#### 5.4 Form Submission

- [ ] POST to `/api/auctions`
- [ ] Loading state on submit button
- [ ] On success: close modal, show toast, refresh list
- [ ] On error: show message, keep form data

**API Contract**:
```typescript
// POST /api/auctions
// Request
{
  "name": string,
  "description": string,
  "startTime": string,     // ISO-8601 instant
  "endTime": string,       // ISO-8601 instant
  "startingPrice": number
}

// Response: 201 Created
"550e8400-e29b-41d4-a716-446655440000"  // UUID as plain text
```

---

### Phase 6: Polish (LOW)

**Priority**: LOW
**Estimated Effort**: 3-4 hours
**Blockers**: Phases 1-5 complete
**Blocks**: None (final phase)

#### 6.1 Error Handling

- [ ] Global error boundary component
- [ ] 404 page for unknown routes
- [ ] Network error states
- [ ] Retry mechanisms

#### 6.2 Loading States

- [ ] Skeleton loaders for auction cards
- [ ] Spinner component for buttons/forms
- [ ] Page-level loading states

#### 6.3 Animations & Transitions

- [ ] Page transitions (subtle)
- [ ] Modal open/close animations
- [ ] Toast slide-in/out
- [ ] Bid update highlight animation
- [ ] Keep all transitions 150-200ms, ease-out

#### 6.4 Accessibility

- [ ] Keyboard navigation for all interactive elements
- [ ] Focus indicators (accent color ring)
- [ ] ARIA labels where needed
- [ ] Minimum touch target 44x44px

#### 6.5 Mobile Responsiveness

- [ ] Test all pages at mobile breakpoint (375px)
- [ ] Collapsible navigation on mobile
- [ ] Full-screen modals on mobile (optional)
- [ ] Touch-friendly bid buttons

#### 6.6 Performance

- [ ] Debounce search input
- [ ] Memoize expensive computations
- [ ] Lazy load routes (code splitting)

---

## Dependencies Diagram

```
BACKEND FIXES (Must complete first)
==================================

[Issue 1: Routing Bug] ─────┐
                            ├──> Blocks Phase 1 & 3 (API calls fail)
[Issue 2: Missing Fields] ──┤
                            │
[Issue 3: Mapper Update] ───┘

[Issue 4: BidRejected Bug] ───> Blocks Phase 4 (wrong user gets errors)

[Issue 5: Lifecycle Scheduler] ──> Does not block frontend
                                   (auctions work, just don't auto-transition)

[Issue 6: Redis Adapter] ──> Does not block (dev uses in-memory)


FRONTEND PHASES
===============

                    ┌─────────────────────┐
                    │  Backend Issues     │
                    │  1, 2, 3 Fixed      │
                    └─────────┬───────────┘
                              │
                              v
                    ┌─────────────────────┐
                    │   Phase 1           │
                    │   Foundation        │
                    │   (CRITICAL)        │
                    └─────────┬───────────┘
                              │
              ┌───────────────┼───────────────┐
              │               │               │
              v               v               v
    ┌─────────────────┐ ┌───────────────┐
    │   Phase 2       │ │   Phase 3     │
    │   Auth          │ │   Browsing    │
    │   (HIGH)        │ │   (HIGH)      │
    └────────┬────────┘ └───────┬───────┘
             │                  │
             │    ┌─────────────┤
             │    │             │
             v    v             v
    ┌─────────────────┐ ┌───────────────┐
    │   Phase 4       │ │   Phase 5     │
    │   Real-Time     │ │   Creation    │
    │   & Bidding     │ │   (MEDIUM)    │
    │   (HIGH)        │ └───────────────┘
    │                 │
    │  Needs Issue 4  │
    │  Fixed          │
    └────────┬────────┘
             │
             v
    ┌─────────────────┐
    │   Phase 6       │
    │   Polish        │
    │   (LOW)         │
    └─────────────────┘
```

---

## API Reference

### Authentication Endpoints

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|--------------|----------|
| POST | `/api/auth/register` | No | `{ username, password, email }` | `201 Created` (empty) |
| POST | `/api/auth/login` | No | `{ username, password }` | `200 OK` JWT string |

### Auction Endpoints

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|--------------|----------|
| GET | `/api/auctions/active` | No | - | `Auction[]` |
| GET | `/api/auctions/{id}` | No | - | `Auction` |
| POST | `/api/auctions` | Yes | `CreateAuctionRequest` | `201 Created` UUID |

### Expected Auction Response (after backend fix)

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Vintage Watch",
  "description": "A beautiful vintage timepiece",
  "startTime": "2024-01-15T10:00:00Z",
  "endTime": "2024-01-15T22:00:00Z",
  "startingPrice": 100.00,
  "highestBid": 150.00,
  "currentWinnerId": "user-uuid-here",
  "status": "ACTIVE"
}
```

---

## WebSocket Message Formats

### Connection

- **Endpoint**: `/ws`
- **Protocol**: STOMP over WebSocket (with SockJS fallback)
- **Auth**: JWT token in `Authorization` header during CONNECT

### Sending Bids

**Destination**: `/app/bid`

```json
{
  "auctionId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 175.00
}
```

### Receiving Bid Updates

**Subscribe to**: `/topic/auctions/{auctionId}`

```json
{
  "bidderId": "user-uuid",
  "amount": 175.00
}
```

### Receiving Outbid Notifications

**Subscribe to**: `/user/queue/notifications`

```json
{
  "uuid": "auction-uuid",
  "amount": 175.00
}
```

### Receiving Bid Rejections

**Subscribe to**: `/user/queue/errors`

```json
{
  "bidderId": "user-uuid",
  "reason": "BID_TOO_LOW"
}
```

**Rejection Reasons**:
- `BID_TOO_LOW` - Another bid was placed before yours
- `AUCTION_CLOSED` - Auction has ended

---

## Summary

### Prioritized Task List (Verified 2026-01-26)

| Priority | Task | Effort | Status | Verified |
|----------|------|--------|--------|----------|
| ~~**CRITICAL**~~ | ~~Issue 1: Fix AuctionController routing~~ | ~~15 min~~ | ✅ Done | ✓ Fixed 2026-01-27 |
| ~~**CRITICAL**~~ | ~~Issue 2: Add missing AuctionResponse fields~~ | ~~15 min~~ | ✅ Done | ✓ Fixed 2026-01-27 |
| ~~**CRITICAL**~~ | ~~Issue 3: Update AuctionMapper~~ | ~~15 min~~ | ✅ Done | ✓ Fixed 2026-01-27 |
| ~~**CRITICAL**~~ | ~~Phase 1: Foundation~~ | ~~2-3 hrs~~ | ✅ Done | ✓ Fixed 2026-01-27 |
| ~~**HIGH**~~ | ~~Issue 4: Fix BidRejectedMessage bug~~ | ~~30 min~~ | ✅ Done | ✓ Fixed 2026-01-27 |
| ~~**HIGH**~~ | ~~Issue 5: Implement Auction Lifecycle Scheduler~~ | ~~1-2 hrs~~ | ✅ Done | ✓ Fixed 2026-01-27 |
| ~~**HIGH**~~ | ~~Phase 2: Authentication~~ | ~~3-4 hrs~~ | ✅ Done | ✓ Fixed 2026-01-27 |
| ~~**HIGH**~~ | ~~Phase 3: Auction Browsing~~ | ~~4-5 hrs~~ | ✅ Done | ✓ Fixed 2026-01-27 |
| ~~**HIGH**~~ | ~~Phase 4: Real-Time & Bidding~~ | ~~5-6 hrs~~ | ✅ Done | ✓ Fixed 2026-01-27 |
| **MEDIUM** | Phase 5: Auction Creation | 3-4 hrs | Not Started | - |
| **MEDIUM** | Issue 7: Registration auto-login spec mismatch | 30 min | Not Started | ✓ Spec vs backend discrepancy |
| **LOW** | Issue 6: Redis Adapter (prod only) | 2-3 hrs | Not Started | ✓ All 3 methods throw UnsupportedOperationException |
| **LOW** | Phase 6: Polish | 3-4 hrs | Not Started | - |

### Estimated Total Effort

| Category | Hours |
|----------|-------|
| Backend Fixes (CRITICAL) | 1-2 |
| Backend Fixes (HIGH) | 2-3 |
| Backend Fixes (MEDIUM) | 0.5 |
| Frontend Phases | 21-28 |
| **Total** | **25-34** |

### Quick Start Checklist

1. [x] Fix backend Issue 1 (routing) - `AuctionController.java` ✅ Done
2. [x] Fix backend Issue 2 (response fields) - `AuctionResponse.java` ✅ Done
3. [x] Fix backend Issue 3 (mapper) - `AuctionMapper.java` ✅ Done
4. [x] Start Phase 1 (foundation) ✅ Done
5. [x] Phase 2: Authentication ✅ Done
6. [ ] Continue with Phases 3-6

### Architecture Notes

**Hexagonal Architecture**: The auction-service follows ports & adapters pattern:
- `application/` - Use cases and ports (interfaces)
- `domain/` - Business logic and entities
- `infrastructure/in/` - Inbound adapters (controllers, listeners)
- `infrastructure/out/` - Outbound adapters (persistence, events)

**Key Files for Backend Fixes**:
- `auction-service/src/main/java/wpessers/auctionservice/auction/infrastructure/in/web/AuctionController.java`
- `auction-service/src/main/java/wpessers/auctionservice/auction/application/port/in/AuctionResponse.java`
- `auction-service/src/main/java/wpessers/auctionservice/auction/application/AuctionMapper.java`
- `auction-service/src/main/java/wpessers/auctionservice/bid/infrastructure/in/spring/SpringBidEventListener.java`

**Price Service**: Currently a skeleton placeholder at `price-service/`. Not required for MVP but designed for future gRPC integration to provide intelligent pricing suggestions.
