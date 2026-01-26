# Bidding

Authenticated users can place bids on active auctions in real-time.

## Behaviors

### Bid Interface
- Bid section visible on auction detail page for authenticated users
- For unauthenticated users, show prompt to log in to bid
- Display current highest bid (or starting price if no bids)
- Display minimum valid bid amount (current highest + $1, or starting price)

### Bid Input
- Quick bid buttons for common increments:
  - "+$5" from current highest
  - "+$10" from current highest
  - "+$50" from current highest
- Custom amount input for precise bidding
- Buttons and input are disabled when auction is not active
- All amounts must be greater than current highest bid

### Placing a Bid
- Clicking a quick bid button or submitting custom amount sends bid via WebSocket
- Show loading/pending state while bid is being processed
- Disable bid controls during submission to prevent double-bids

### Bid Feedback
- On successful bid:
  - Update display to show new highest bid
  - Show success indicator (e.g., green flash, "You're winning!")
  - Re-enable bid controls
- On bid rejection (BID_TOO_LOW):
  - Show error message explaining bid was too low
  - Update current highest bid (someone else bid higher)
  - Re-enable bid controls
- On bid rejection (AUCTION_CLOSED):
  - Show message that auction has ended
  - Disable bid controls
  - Update auction status display

### Validation
- Client-side validation before sending:
  - Amount must be a valid positive number
  - Amount must be greater than current highest bid
- Show inline error for invalid amounts
- Quick bid buttons always calculate valid amounts

### Self-Outbidding
- Users can increase their own bid even if already winning
- No special restriction on bidding against yourself

## Acceptance Criteria

- [ ] Bid section shows current highest bid amount
- [ ] Quick bid buttons display with correct increment amounts
- [ ] Quick bid buttons are calculated based on current highest bid
- [ ] Custom input accepts valid bid amounts
- [ ] Invalid bid amounts show error before submission
- [ ] Successful bid updates UI to show new highest amount
- [ ] BID_TOO_LOW rejection shows error and updates current bid
- [ ] AUCTION_CLOSED rejection disables bidding interface
- [ ] Bid controls disabled during submission
- [ ] Unauthenticated users see login prompt instead of bid controls

## WebSocket Integration

### Sending Bids
- Destination: `/app/bid`
- Payload: `{ "auctionId": "uuid", "amount": number }`

### Receiving Updates
- Subscribe to `/topic/auctions/{auctionId}` for bid updates
- Subscribe to `/user/queue/errors` for bid rejections
- Subscribe to `/user/queue/notifications` for outbid alerts (covered in real-time-updates spec)

## Edge Cases

- Rapid bid updates: UI must handle multiple updates in quick succession
- Stale bid: User's bid may be rejected if someone else bid while they were typing
- Network latency: Optimistic UI update vs waiting for confirmation
- Auction ending while bidding: Handle gracefully with clear messaging
