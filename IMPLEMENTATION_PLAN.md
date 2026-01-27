# Auction Platform Implementation Plan

This document serves as a reference for the Auction Platform architecture, APIs, and development status.

**Last Updated:** 2026-01-27

---

## Table of Contents

1. [Current Status Summary](#current-status-summary)
2. [Completed Work Summary](#completed-work-summary)
3. [Future Work](#future-work)
4. [Dependencies Diagram](#dependencies-diagram)
5. [API Reference](#api-reference)
6. [WebSocket Message Formats](#websocket-message-formats)
7. [Architecture Notes](#architecture-notes)

---

## Current Status Summary

### Overall Progress

| Area | Status | Notes |
|------|--------|-------|
| **Backend Core** | ✅ 100% complete | All issues resolved including Redis adapter |
| **Frontend** | ✅ 100% complete | All 6 phases implemented |
| **Specs** | 100% complete | All 6 specs comprehensive and ready |
| **Price Service** | Not implemented | Skeleton only - gRPC service placeholder |

**MVP is complete.** The application is feature-complete and production-ready.

### Backend Status (Verified 2026-01-27)

| Component | Status | Notes |
|-----------|--------|-------|
| Authentication | ✅ Working | JWT-based auth with auto-login on registration |
| Auction CRUD | ✅ Working | Fixed routing, complete response fields |
| Bidding (WebSocket) | ✅ Working | Real-time bidding with proper error handling |
| Auction Lifecycle | ✅ Working | Scheduler auto-transitions auction states |
| Redis Registry | ✅ Working | Production-ready horizontal scaling |

### Frontend Status (Verified 2026-01-27)

```
frontend/
├── src/
│   ├── api/               # API client with auth integration
│   ├── components/        # Reusable UI components
│   │   ├── auctions/      # Auction cards, creation modal, detail views
│   │   ├── bidding/       # Bidding panel with real-time updates
│   │   ├── auth/          # Protected routes
│   │   ├── layout/        # Root layout with navigation
│   │   └── ui/            # Toast, modals, loading states, error boundary
│   ├── context/           # Auth and WebSocket contexts
│   ├── hooks/             # useCountdown, useDebounce
│   ├── pages/             # All main pages (Auth, Auctions, Profile)
│   ├── services/          # WebSocket service with auto-reconnect
│   ├── types/             # TypeScript type definitions
│   ├── router.tsx         # React Router configuration
│   └── index.css          # Dark theme with CSS variables
├── package.json           # React 19.2.3, Tailwind 4.1.18, Vite 7.2.4
└── vite.config.ts         # Path aliases, Vitest config
```

### Specs Available

All specs exist in `/specs/`:
- `authentication.md` - Auth flows and JWT handling
- `auction-browsing.md` - List and detail views
- `auction-creation.md` - Create auction form
- `bidding.md` - Bid submission and validation
- `real-time-updates.md` - WebSocket integration
- `ui-design-system.md` - Dark theme, colors, typography

---

## Completed Work Summary

### Backend Fixes (All Resolved)

All critical backend issues have been resolved:

1. **AuctionController Routing** - Fixed endpoint ordering to prevent path conflicts
2. **AuctionResponse Fields** - Added `highestBid`, `currentWinnerId`, and `status` fields
3. **AuctionMapper Updates** - Updated mapper to include all response fields with proper null handling
4. **BidRejectedMessage Fix** - Corrected to send `bidderId` instead of `auctionId`
5. **Auction Lifecycle Scheduler** - Implemented automatic state transitions for auctions (scheduled → active → closed)
6. **Redis Registry Adapter** - Implemented distributed locking for production horizontal scaling
7. **Registration Auto-Login** - Backend now returns JWT token on registration for seamless UX

### Frontend Implementation (All Phases Complete)

All 6 development phases have been completed:

1. **Phase 1: Foundation** - Project structure, routing, API client, dark theme, path aliases
2. **Phase 2: Authentication** - Login, registration, JWT handling, protected routes, auth context
3. **Phase 3: Auction Browsing** - Auction list, detail views, search, countdown timers
4. **Phase 4: Real-Time & Bidding** - WebSocket integration, live updates, bidding panel, notifications
5. **Phase 5: Auction Creation** - Create auction form with validation, floating action button
6. **Phase 6: Polish** - Error boundaries, loading states, animations, accessibility, mobile responsiveness

---

## Future Work

### Price Service Integration

The next major feature for development is the **Price Service**, which currently exists as a skeleton placeholder:

- **Location**: `/price-service/`
- **Purpose**: Provide intelligent pricing suggestions using gRPC
- **Current Status**: Placeholder only - not implemented
- **Integration Point**: Would connect to auction service via gRPC for real-time price analysis
- **Benefits**: AI-powered bid recommendations, market analysis, pricing insights

### Potential Enhancements

Additional features to consider for future releases:

- User profile management (edit email, change password)
- Auction categories and filtering
- Advanced search with filters (price range, time remaining, status)
- Auction watchlist / favorites
- Bid history for users
- Email notifications for auction events
- Image uploads for auctions
- Payment integration
- Auction analytics dashboard

---

## Dependencies Diagram

```
PRODUCTION ARCHITECTURE
=======================

┌─────────────────────────────────────────────────────┐
│                   Frontend (React)                  │
│  - React 19.2.3 + TypeScript                       │
│  - Tailwind CSS 4 (Dark Theme)                     │
│  - React Router 7                                   │
│  - WebSocket (STOMP + SockJS)                      │
└──────────────┬────────────────────┬─────────────────┘
               │                    │
               │ HTTP/REST          │ WebSocket
               │                    │
               v                    v
┌──────────────────────────────────────────────────────┐
│           Auction Service (Spring Boot)              │
│  - Authentication (JWT)                             │
│  - Auction CRUD                                      │
│  - Bidding Engine                                    │
│  - Lifecycle Scheduler                               │
└───────┬────────────────────┬─────────────────────────┘
        │                    │
        │ PostgreSQL         │ Redis (prod)
        │                    │
        v                    v
┌──────────────┐    ┌─────────────────┐
│  PostgreSQL  │    │  Redis Registry │
│   Database   │    │ (Distributed    │
│              │    │  Locking)       │
└──────────────┘    └─────────────────┘

FUTURE INTEGRATION
==================

┌──────────────────────────────────────────────────────┐
│           Auction Service (Spring Boot)              │
└──────────────────────────┬───────────────────────────┘
                           │
                           │ gRPC
                           │
                           v
                  ┌─────────────────┐
                  │  Price Service  │
                  │  (gRPC Server)  │
                  │                 │
                  │  - Pricing AI   │
                  │  - Suggestions  │
                  └─────────────────┘
```

---

## API Reference

### Authentication Endpoints

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|--------------|----------|
| POST | `/api/auth/register` | No | `{ username, password, email }` | `201 Created` JWT string |
| POST | `/api/auth/login` | No | `{ username, password }` | `200 OK` JWT string |

### Auction Endpoints

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|--------------|----------|
| GET | `/api/auctions/active` | No | - | `Auction[]` |
| GET | `/api/auctions/{id}` | No | - | `Auction` |
| POST | `/api/auctions` | Yes | `CreateAuctionRequest` | `201 Created` UUID |

### Auction Response Format

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

### Create Auction Request

```json
{
  "name": "Vintage Watch",
  "description": "A beautiful vintage timepiece",
  "startTime": "2024-01-15T10:00:00Z",
  "endTime": "2024-01-15T22:00:00Z",
  "startingPrice": 100.00
}
```

### JWT Token Structure

Tokens contain the following claims:
- `sub` - Username
- `userId` - User UUID
- `exp` - Expiration timestamp

Frontend can parse JWT client-side to extract user information:

```typescript
function parseJwt(token: string): { userId: string; username: string } {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const payload = JSON.parse(atob(base64));
  return { userId: payload.userId, username: payload.sub };
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

Updates are broadcast to all subscribers when a new bid is placed.

### Receiving Outbid Notifications

**Subscribe to**: `/user/queue/notifications`

```json
{
  "uuid": "auction-uuid",
  "amount": 175.00
}
```

Personal notifications sent when another user outbids you.

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

## Architecture Notes

### Hexagonal Architecture

The auction-service follows the ports & adapters pattern:

- `application/` - Use cases and ports (interfaces)
- `domain/` - Business logic and entities
- `infrastructure/in/` - Inbound adapters (controllers, WebSocket listeners)
- `infrastructure/out/` - Outbound adapters (persistence, events)

This architecture provides:
- Clear separation of concerns
- Easy testing (mock ports)
- Framework independence
- Flexibility to swap implementations

### Key Backend Components

**Controllers** (HTTP Entry Points):
- `AuctionController.java` - REST endpoints for auction CRUD
- `UserAuthController.java` - Authentication endpoints

**WebSocket Components**:
- `SpringBidEventListener.java` - Broadcasts bid events to subscribers
- `BiddingController.java` - Handles incoming bid messages

**Schedulers**:
- `AuctionLifecycleScheduler.java` - Auto-transitions auction states every 30 seconds

**Registry Adapters**:
- `InMemoryAuctionRegistry.java` - Development/testing (default)
- `RedisAuctionRegistryAdapter.java` - Production (with `@Profile("prod")`)

### Frontend Architecture

**Key Patterns**:
- Context API for global state (Auth, WebSocket, Toast)
- Custom hooks for reusable logic (useCountdown, useDebounce)
- Component composition with clear responsibilities
- Type-safe API client with automatic auth headers

**State Management**:
- AuthContext - User authentication state
- WebSocketContext - Real-time connection management
- ToastContext - Global notification system

**WebSocket Strategy**:
- Auto-reconnect with exponential backoff
- Connection state tracking
- Automatic cleanup on unmount
- Token-based authentication

### Production Deployment

**Development Mode** (default):
- Uses in-memory auction registry
- No Redis required
- Single-instance deployment

**Production Mode** (`spring.profiles.active=prod`):
- Uses Redis for distributed state
- Supports horizontal scaling
- Multiple instances can run simultaneously
- Distributed locking ensures consistency

### Environment Configuration

**Backend**:
- `application.yml` - Default configuration
- `application-prod.yml` - Production overrides (Redis, etc.)

**Frontend**:
- `.env.development` - Local backend URLs
- `.env.production` - Production backend URLs

---

## Summary

The Auction Platform MVP is complete with all core features implemented:

- User authentication with JWT
- Real-time auction browsing with search
- Live bidding with WebSocket updates
- Auction creation with validation
- Responsive dark-themed UI
- Production-ready with Redis scaling

The application is ready for deployment and use. Future work focuses on the Price Service integration and optional enhancements.
