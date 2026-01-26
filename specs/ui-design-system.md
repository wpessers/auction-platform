# UI Design System

The app uses a consistent, modern, minimalistic design system with a dark theme.

## Visual Foundation

### Color Scheme (Dark Mode)

**Background Colors:**
- Primary background: Dark gray (`#0a0a0a` or similar)
- Secondary background: Slightly lighter (`#171717`)
- Card/surface background: Elevated surface (`#262626`)
- Border color: Subtle (`#404040`)

**Text Colors:**
- Primary text: Near-white (`#fafafa`)
- Secondary text: Muted gray (`#a3a3a3`)
- Disabled text: Dim (`#525252`)

**Accent Colors (Green):**
- Primary accent: Vibrant green (`#22c55e` - Tailwind green-500)
- Accent hover: Lighter green (`#4ade80` - green-400)
- Accent muted: For backgrounds (`#166534` - green-800 at low opacity)

**Semantic Colors:**
- Success: Green (same as accent)
- Error: Red (`#ef4444`)
- Warning: Amber (`#f59e0b`)
- Info: Blue (`#3b82f6`)

### Typography

**Font Family:**
- Primary: System font stack (San Francisco, Segoe UI, Roboto, etc.)
- Monospace: For prices/numbers (optional)

**Scale:**
- Headings: Bold, larger sizes for hierarchy
- Body: Regular weight, comfortable reading size (16px base)
- Small: For secondary information, timestamps

**Principles:**
- High contrast text on dark backgrounds
- Clear hierarchy through size and weight, not just color

### Spacing

**Spacious Layout:**
- Generous padding inside cards and containers
- Comfortable margins between sections
- Breathing room around interactive elements
- Minimum touch target size: 44x44px for mobile

**Grid:**
- Responsive grid for auction cards (1 column mobile, 2-3 tablet, 4 desktop)
- Consistent gap between grid items

### Components

**Buttons:**
- Primary: Green background, white text, rounded
- Secondary: Transparent with border, green text
- Disabled: Muted colors, reduced opacity
- Hover: Subtle brightness increase
- No heavy shadows or 3D effects

**Cards:**
- Elevated surface color
- Subtle border or shadow for depth
- Rounded corners (8-12px)
- Consistent padding

**Forms:**
- Dark input backgrounds (darker than card surface)
- Clear focus states with accent color ring
- Inline validation errors in red
- Labels above inputs

**Modals:**
- Centered overlay with backdrop blur
- Card-like surface
- Clear close button
- Smooth open/close transitions (subtle, not over-the-top)

**Toasts/Notifications:**
- Appear in top-right or bottom-right
- Colored left border indicating type (success/error/info)
- Auto-dismiss with progress indicator (optional)
- Stack vertically

**Navigation:**
- Fixed header with logo, nav items, profile menu
- Subtle bottom border or shadow separation
- FAB positioned bottom-right, green accent

### Interactions

**Transitions:**
- Subtle and quick (150-200ms)
- Ease-out timing function
- No bouncy or spring animations
- Hover states on interactive elements

**Feedback:**
- Loading spinners for async operations
- Disabled states during submission
- Success/error states clearly visible
- Focus indicators for keyboard navigation

### Responsive Design

**Breakpoints:**
- Mobile-first approach
- sm: 640px
- md: 768px
- lg: 1024px
- xl: 1280px

**Adaptations:**
- Single column layout on mobile
- Collapsible navigation on mobile
- Touch-friendly targets
- Modal becomes full-screen on mobile (optional)

## Acceptance Criteria

- [ ] Dark theme applied consistently across all pages
- [ ] Green accent color used for primary actions and highlights
- [ ] Spacious layout with generous whitespace
- [ ] Typography hierarchy is clear and readable
- [ ] All interactive elements have hover/focus states
- [ ] Transitions are subtle and quick (no flashy animations)
- [ ] Responsive layout works on mobile, tablet, and desktop
- [ ] Forms have clear validation feedback
- [ ] Loading states visible during async operations
- [ ] Toast notifications styled consistently

## Implementation Notes

- Use Tailwind CSS for styling (already configured)
- Extend Tailwind config with custom colors if needed
- Create reusable component library in `src/components/`
- Consider using Tailwind's `dark:` variants even for dark-only (future flexibility)

## Visual Validation

Component tests verify behavior but cannot verify visual appearance. Use Playwright screenshots to validate UI implementation:

**Workflow:**
1. Start dev server: `cd frontend && npm run dev &`
2. Capture screenshot: `cd frontend && npx playwright screenshot http://localhost:5173 /tmp/screenshot.png`
3. View screenshot with Read tool to verify:
   - Dark theme colors are correct
   - Spacing and layout match spec
   - Typography hierarchy is clear
   - Components render as designed

**Validation Checklist (per screenshot):**
- Background colors are dark (#0a0a0a base)
- Text is high-contrast and readable
- Green accent (#22c55e) used for primary actions
- Cards have elevated surface color
- Spacing is generous (not cramped)
- No visual glitches or broken layouts

**Responsive Testing:**
Capture at different viewport sizes to verify responsive behavior:
- Mobile: 375px width
- Tablet: 768px width
- Desktop: 1280px width

## Reference Palette (Tailwind)

```js
// tailwind.config.js extension (if needed)
colors: {
  background: '#0a0a0a',
  surface: '#171717',
  card: '#262626',
  border: '#404040',
  accent: {
    DEFAULT: '#22c55e',
    hover: '#4ade80',
    muted: '#166534',
  }
}
```
