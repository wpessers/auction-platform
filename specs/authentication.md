# Authentication

Users can register, log in, and manage their session to access protected features.

## Behaviors

### Registration
- User can navigate to a registration form from the login page
- Registration form collects: username, password, email
- Form validates inputs before submission:
  - Username: required, non-empty
  - Password: required, non-empty
  - Email: required, valid email format
- On successful registration, user is automatically logged in and redirected to the auctions list
- On failure, display the error message from the backend (e.g., "Username already exists")

### Login
- User can access login form from the landing page or when redirected from protected routes
- Login form collects: username, password
- On successful login:
  - Store JWT token in localStorage (persists across browser sessions)
  - Redirect to the auctions list (or the page they were trying to access)
- On failure, display error message (e.g., "Invalid credentials")

### Session Management
- JWT token is included in Authorization header for all protected API requests
- JWT token is included in WebSocket CONNECT frame for real-time features
- If a request returns 401 Unauthorized, clear the token and redirect to login
- Token persists until user logs out or token expires (24 hours)

### Logout
- User can log out from the profile dropdown/menu
- Logout clears the JWT token from localStorage
- Logout disconnects any active WebSocket connections
- User is redirected to the login page

### Profile
- Authenticated users can view their profile showing username and email
- Profile is accessible from a header menu/dropdown
- Profile page shows a logout button

## Acceptance Criteria

- [ ] Unauthenticated users are redirected to login when accessing protected routes
- [ ] Registration form shows validation errors for invalid inputs
- [ ] Registration with duplicate username/email shows backend error message
- [ ] Successful registration logs user in automatically
- [ ] Login persists across browser tab closes and reopens
- [ ] 401 responses trigger automatic logout and redirect to login
- [ ] Logout clears all auth state and WebSocket connections
- [ ] Profile page displays current user's username and email

## API Integration

| Action | Endpoint | Auth Required |
|--------|----------|---------------|
| Register | `POST /api/auth/register` | No |
| Login | `POST /api/auth/login` | No |
| Profile data | Decoded from JWT (userId, username) | N/A |

Note: Email is not stored in JWT, so profile may only show username unless we store email client-side after registration.
