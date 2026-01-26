# Auction Creation

Authenticated users can create new auctions with customizable time windows and starting prices.

## Behaviors

### Access
- Floating action button (FAB) visible on the auction list page for authenticated users
- FAB is hidden for unauthenticated users
- Clicking FAB opens the auction creation modal

### Creation Form (Modal)
- Modal overlay with form containing:
  - Name (text input, required)
  - Description (textarea, required)
  - Start time (datetime picker, required, must be now or in the future)
  - End time (datetime picker, required, must be after start time)
  - Starting price (number input, required, must be >= 0)
- Form validates inputs before submission
- Cancel button closes modal without saving
- Submit button creates the auction

### Validation
- Client-side validation:
  - Name: required, non-empty
  - Description: required, non-empty
  - Start time: required, must be present or future
  - End time: required, must be after start time
  - Starting price: required, must be a valid number >= 0
- Display inline validation errors below each field
- Disable submit button while form is invalid or submitting

### Submission
- On submit, show loading state on button
- On success:
  - Close modal
  - Show success toast/notification
  - Refresh auction list (new auction appears if start time is now)
  - Optionally navigate to the new auction detail page
- On failure:
  - Display error message in modal
  - Keep form open with user's input preserved

### Quick Start Option
- Provide a "Start now" checkbox or button that sets start time to current time
- When enabled, start time picker can be hidden or disabled

## Acceptance Criteria

- [ ] FAB is visible only to authenticated users on auction list page
- [ ] Clicking FAB opens creation modal
- [ ] All form fields are present with appropriate input types
- [ ] Form shows validation errors for invalid inputs
- [ ] Start time cannot be in the past
- [ ] End time must be after start time
- [ ] Starting price accepts 0 or positive numbers
- [ ] Successful creation closes modal and shows confirmation
- [ ] Failed creation shows error message and preserves form data
- [ ] Newly created auction appears in list (if active)

## API Integration

| Action | Endpoint | Auth Required |
|--------|----------|---------------|
| Create auction | `POST /api/auctions` | Yes |

### Request Body
```json
{
  "name": "string",
  "description": "string",
  "startTime": "ISO-8601 instant",
  "endTime": "ISO-8601 instant",
  "startingPrice": "number"
}
```

### Response
- `201 Created` with auction UUID in response body
- `400 Bad Request` with validation errors
- `401 Unauthorized` if not authenticated
