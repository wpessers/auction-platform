# Auction Browsing

Users can browse active auctions and view detailed information about individual auctions.

## Behaviors

### Auction List
- Landing page (after login) displays all active auctions in a card grid layout
- Each auction card shows:
  - Auction name
  - Time remaining (countdown or "Ending soon" indicator)
  - Current highest bid (or starting price if no bids yet)
  - Visual indicator that auction is active
- Cards are clickable and navigate to the auction detail page
- List refreshes automatically when bid updates are received via WebSocket
- Empty state shown when no active auctions exist

### Search
- Search input filters auctions by name (client-side filtering)
- Search is case-insensitive
- Results update as user types (debounced)
- Clear button resets search and shows all auctions
- Empty state shown when search yields no results

### Auction Detail Page
- Displays full auction information:
  - Name
  - Description
  - Start time
  - End time
  - Time remaining (live countdown)
  - Starting price
  - Current highest bid (updated in real-time)
- Shows bid history or at minimum the current winning bid
- Provides interface to place a bid (covered in bidding spec)
- Back navigation to auction list

### Time Display
- Time remaining shows in human-readable format:
  - "> 1 day": "2 days left"
  - "< 1 day": "5 hours 23 minutes left"
  - "< 1 hour": "45 minutes left"
  - "< 5 minutes": "4:32" (countdown timer, more urgent styling)
- Auction cards/detail update countdown in real-time (every second for < 5 min, every minute otherwise)

## Acceptance Criteria

- [ ] Auction list loads and displays all active auctions on page load
- [ ] Each auction card displays name, time remaining, and current price
- [ ] Clicking an auction card navigates to detail page with full information
- [ ] Search filters auctions by name as user types
- [ ] Time remaining updates live without page refresh
- [ ] Empty states display appropriate messages
- [ ] Auction detail shows all fields: name, description, times, prices
- [ ] Back navigation returns to auction list preserving search state

## API Integration

| Action | Endpoint | Auth Required |
|--------|----------|---------------|
| List active auctions | `GET /api/auctions/active` | No |
| Get auction detail | `GET /api/auctions/{id}` | No |

## Real-time Updates

Auction list and detail pages subscribe to WebSocket topics to receive live bid updates (detailed in real-time-updates spec).
