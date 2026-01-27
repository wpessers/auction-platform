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
| **Price Service** | ✅ 100% complete | Full gRPC integration with intelligent pricing |

**MVP is complete.** The application is feature-complete and production-ready with AI-powered pricing capabilities.

### Backend Status (Verified 2026-01-27)

| Component | Status | Notes |
|-----------|--------|-------|
| Authentication | ✅ Working | JWT-based auth with auto-login on registration |
| Auction CRUD | ✅ Working | Fixed routing, complete response fields |
| Bidding (WebSocket) | ✅ Working | Real-time bidding with proper error handling |
| Auction Lifecycle | ✅ Working | Scheduler auto-transitions auction states |
| Redis Registry | ✅ Working | Production-ready horizontal scaling |
| Price Service | ✅ Working | gRPC-based intelligent pricing with REST API |

### Frontend Status (Verified 2026-01-27)

```
frontend/
├── src/
│   ├── api/               # API client with auth integration
│   ├── components/        # Reusable UI components
│   │   ├── auctions/      # Auction cards, creation modal, detail views
│   │   ├── bidding/       # Bidding panel with real-time updates
│   │   ├── pricing/       # AI-powered pricing suggestions
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

### Price Service Implementation

The Price Service has been fully implemented as a standalone gRPC microservice with complete integration into the auction platform:

**Core Components:**
- **gRPC Proto File** - `pricing.proto` defining `PricingService` with two RPCs:
  - `GetBidSuggestion` - Returns intelligent bid recommendations based on auction data
  - `AnalyzeAuction` - Provides comprehensive market analysis and pricing insights

- **Price Service gRPC Server** (Kotlin) - Standalone microservice with intelligent pricing algorithms:
  - Statistical analysis of bid patterns and market trends
  - Dynamic bid increment calculations based on auction phase
  - Confidence scoring for pricing recommendations
  - Market positioning analysis

- **Auction Service gRPC Client** - Integration adapter with robust error handling:
  - gRPC client adapter for Price Service communication
  - Fallback handling for service unavailability
  - Graceful degradation when pricing service is offline

- **REST API Endpoints** - Exposed pricing functionality via HTTP:
  - `GET /api/pricing/auctions/{id}/suggestions` - Returns `BidSuggestion` with recommended amounts
  - `GET /api/pricing/auctions/{id}/analysis` - Returns `AuctionAnalysis` with market insights

- **Frontend Integration** - `PricingSuggestions` component:
  - Displays AI-powered bid recommendations in real-time
  - Visual confidence indicators for suggestions
  - Seamless integration with bidding panel
  - Automatic updates based on auction activity

**Benefits:**
- AI-powered bid recommendations improve user decision-making
- Market analysis provides transparency and insights
- gRPC communication ensures low-latency pricing data
- Microservice architecture enables independent scaling

### Profile API Implementation

The user profile API has been implemented to enable users to view their account information:

**Backend Components:**
1. **New Endpoint** - `GET /api/users/me` for fetching authenticated user profile with email
2. **UserProfileController** - New REST controller handling profile requests
3. **UserProfileResponse** - DTO containing userId, username, and email
4. **UserNotFoundException** - Exception thrown when user ID from JWT doesn't exist
5. **UserStorage Port Update** - Added `findById` method to retrieve user by UUID

**Frontend Components:**
1. **ProfilePage Enhancement** - Now fetches and displays user email from API instead of JWT
2. **JWT Parsing Fix** - Corrected token parsing to extract proper fields from JWT payload (userId from `userId` claim, username from `sub` claim)

**Benefits:**
- Users can view complete profile information including email
- Centralized user data fetching from backend (single source of truth)
- Fixed JWT parsing ensures correct user identification
- Foundation for future profile management features

---

## Future Work

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
- Historical price trends and analytics

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
│  - Pricing Suggestions Component                    │
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
│  - Pricing REST API (proxy to Price Service)        │
└───────┬────────────────────┬──────────────┬──────────┘
        │                    │              │
        │ PostgreSQL         │ Redis        │ gRPC
        │                    │              │
        v                    v              v
┌──────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  PostgreSQL  │    │  Redis Registry │    │  Price Service  │
│   Database   │    │ (Distributed    │    │  (Kotlin/gRPC)  │
│              │    │  Locking)       │    │                 │
└──────────────┘    └─────────────────┘    │  - Pricing AI   │
                                            │  - Market       │
                                            │    Analysis     │
                                            └─────────────────┘
```

---

## API Reference

### Authentication Endpoints

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|--------------|----------|
| POST | `/api/auth/register` | No | `{ username, password, email }` | `201 Created` JWT string |
| POST | `/api/auth/login` | No | `{ username, password }` | `200 OK` JWT string |

### User Endpoints

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|--------------|----------|
| GET | `/api/users/me` | Yes | - | `UserProfileResponse` |

### Auction Endpoints

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|--------------|----------|
| GET | `/api/auctions/active` | No | - | `Auction[]` |
| GET | `/api/auctions/{id}` | No | - | `Auction` |
| POST | `/api/auctions` | Yes | `CreateAuctionRequest` | `201 Created` UUID |

### Pricing Endpoints

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|--------------|----------|
| GET | `/api/pricing/auctions/{id}/suggestions` | No | - | `BidSuggestion` |
| GET | `/api/pricing/auctions/{id}/analysis` | No | - | `AuctionAnalysis` |

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

### User Profile Response

```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john_doe",
  "email": "john@example.com"
}
```

### Bid Suggestion Response

```json
{
  "auctionId": "550e8400-e29b-41d4-a716-446655440000",
  "suggestedBids": [
    {
      "amount": 155.00,
      "confidence": "HIGH",
      "reasoning": "Competitive bid with strong winning potential"
    },
    {
      "amount": 160.00,
      "confidence": "MEDIUM",
      "reasoning": "Aggressive bid for immediate lead"
    }
  ],
  "minimumBid": 151.00,
  "timestamp": "2024-01-15T12:30:00Z"
}
```

### Auction Analysis Response

```json
{
  "auctionId": "550e8400-e29b-41d4-a716-446655440000",
  "biddingActivity": "HIGH",
  "priceVelocity": 25.5,
  "estimatedFinalPrice": 200.00,
  "marketPosition": "COMPETITIVE",
  "insights": [
    "High bidding activity indicates strong interest",
    "Price increasing 25% faster than average"
  ]
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
- `infrastructure/out/` - Outbound adapters (persistence, events, gRPC clients)

This architecture provides:
- Clear separation of concerns
- Easy testing (mock ports)
- Framework independence
- Flexibility to swap implementations

### Key Backend Components

**Controllers** (HTTP Entry Points):
- `AuctionController.java` - REST endpoints for auction CRUD
- `UserAuthController.java` - Authentication endpoints
- `PricingController.java` - Pricing endpoints (proxy to Price Service)

**WebSocket Components**:
- `SpringBidEventListener.java` - Broadcasts bid events to subscribers
- `BiddingController.java` - Handles incoming bid messages

**Schedulers**:
- `AuctionLifecycleScheduler.java` - Auto-transitions auction states every 30 seconds

**Registry Adapters**:
- `InMemoryAuctionRegistry.java` - Development/testing (default)
- `RedisAuctionRegistryAdapter.java` - Production (with `@Profile("prod")`)

**gRPC Integration**:
- `PriceServiceGrpcAdapter.java` - Client adapter for Price Service communication with fallback handling

### Price Service Architecture

**Microservice Components**:
- `PricingServiceImpl.kt` - gRPC service implementation with intelligent algorithms
- `pricing.proto` - Protocol buffer definitions
- Independent Kotlin/gRPC server with dedicated port

**Integration Pattern**:
- Auction service acts as gRPC client
- REST API proxies requests to Price Service
- Graceful degradation if Price Service unavailable
- Low-latency communication via gRPC

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
- Price Service optional (graceful fallback)

**Production Mode** (`spring.profiles.active=prod`):
- Uses Redis for distributed state
- Supports horizontal scaling
- Multiple instances can run simultaneously
- Distributed locking ensures consistency
- Price Service recommended for full feature set

### Environment Configuration

**Backend**:
- `application.yml` - Default configuration
- `application-prod.yml` - Production overrides (Redis, etc.)

**Frontend**:
- `.env.development` - Local backend URLs
- `.env.production` - Production backend URLs

---

## Summary

The Auction Platform MVP is complete with all core features and AI-powered pricing capabilities:

- User authentication with JWT
- Real-time auction browsing with search
- Live bidding with WebSocket updates
- Auction creation with validation
- Responsive dark-themed UI
- Production-ready with Redis scaling
- AI-powered bid suggestions and market analysis
- gRPC-based Price Service microservice

The application is fully production-ready and feature-complete. Future work focuses on optional enhancements like user profile management, advanced filtering, and analytics dashboards.
