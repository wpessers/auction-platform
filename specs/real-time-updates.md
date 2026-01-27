# Real-time Updates

Users see bid updates and notifications without refreshing the page.

## Behaviors

### WebSocket Connection
- Establish WebSocket connection when user is authenticated
- Connect to `/ws` endpoint with JWT token in Authorization header
- Connection is established on app load (after login) and maintained throughout session
- Connection is closed on logout

### Connection Resilience
- Auto-reconnect silently on disconnect
- Use exponential backoff for reconnection attempts
- Show subtle connection indicator only if disconnected for > 5 seconds
- Resume subscriptions automatically after reconnection
- No user action required for reconnection

### Auction List Updates
- Subscribe to bid updates for all displayed auctions
- When a bid is placed on any auction in the list:
  - Update the card to show new highest bid
  - Optionally show subtle animation to draw attention to changed card
- Updates happen without full page refresh

### Auction Detail Updates
- Subscribe to `/topic/auctions/{auctionId}` when viewing auction detail
- Real-time updates to:
  - Current highest bid amount
  - Bidder information (if displayed)
- Unsubscribe when navigating away from auction detail

### Outbid Notifications
- Subscribe to `/user/queue/notifications` for personal notifications
- When user is outbid:
  - Show toast notification with message: "You've been outbid on [Auction Name]!"
  - Toast includes the new bid amount
  - Toast is clickable and navigates to the auction
  - Toast auto-dismisses after 5 seconds
  - Multiple toasts stack (don't replace each other)

### Bid Rejection Messages
- Subscribe to `/user/queue/errors` for bid rejection feedback
- Display error inline in the bid interface (covered in bidding spec)
- Reasons:
  - `BID_TOO_LOW`: "Your bid was too low. Current highest is $X"
  - `AUCTION_CLOSED`: "This auction has ended"

### Auction Lifecycle Events
- When an auction ends (time expires):
  - Update auction status display to "Closed" or "Ended"
  - Disable bidding controls
  - Show appropriate message to winner vs other bidders
- Note: Backend may not push auction end events; client may need to handle via countdown timer

## Acceptance Criteria

- [ ] WebSocket connection established on authenticated app load
- [ ] Connection auto-reconnects silently on disconnect
- [ ] Prolonged disconnect (>5s) shows subtle indicator
- [ ] Auction list cards update in real-time when bids are placed
- [ ] Auction detail page updates in real-time
- [ ] Outbid notification appears as toast with auction name and amount
- [ ] Clicking outbid toast navigates to the auction
- [ ] Toasts auto-dismiss after 5 seconds
- [ ] Bid rejection errors display in bid interface
- [ ] WebSocket disconnects cleanly on logout

## WebSocket Subscriptions

| Destination | Purpose | Scope |
|-------------|---------|-------|
| `/topic/auctions/{id}` | Bid updates for specific auction | Per-auction |
| `/user/queue/notifications` | Outbid alerts | User-specific |
| `/user/queue/errors` | Bid rejections | User-specific |

## Message Formats

### BidPlacedMessage (from `/topic/auctions/{id}`)
```json
{
  "bidderId": "uuid",
  "amount": 150.00
}
```

### OutbidMessage (from `/user/queue/notifications`)
```json
{
  "uuid": "auction-uuid",
  "auctionName": "Vintage Watch",
  "amount": 150.00
}
```

### BidRejectedMessage (from `/user/queue/errors`)
```json
{
  "bidderId": "uuid",
  "reason": "BID_TOO_LOW" | "AUCTION_CLOSED"
}
```

## Technical Considerations

- Use STOMP protocol over WebSocket
- Consider using a WebSocket client library (e.g., @stomp/stompjs)
- Implement connection state management in global app state
- Handle token expiry during long sessions (reconnect with new token if needed)
