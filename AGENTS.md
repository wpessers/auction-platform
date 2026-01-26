## Build & Run

### Infrastructure
- Start Postgres: `docker-compose up -d`
- Stop Postgres: `docker-compose down`

### Backend
- Build: `./gradlew build`
- Run auction-service: `./gradlew :auction-service:bootRun`
- Run price-service: `./gradlew :price-service:bootRun`

### Frontend
- Install: `cd frontend && npm install`
- Dev server: `cd frontend && npm run dev`
- Build: `cd frontend && npm run build`

## Validation

### Backend
- All Tests: `./gradlew test`
- Single class: `./gradlew :auction-service:test --tests "*ClassName"`
- Single method: `./gradlew :auction-service:test --tests "*ClassName.methodName"`
- Package: `./gradlew :auction-service:test --tests "wpessers.auctionservice.auction.*"`
- Pattern: `./gradlew :auction-service:test --tests "*Controller*"`
- Lint: `./gradlew check`

### Frontend
- All tests: `cd frontend && npm test`
- Watch mode: `cd frontend && npm run test:watch`
- Type check: `cd frontend && npm run lint`

### Visual Validation (Frontend)
To verify UI appearance, use Playwright to capture screenshots while dev server is running:

1. Start dev server in background: `cd frontend && npm run dev &`
2. Wait for server to be ready (typically port 5173)
3. Capture screenshot: `cd frontend && npx playwright screenshot http://localhost:5173 /tmp/screenshot.png`
4. View screenshot using Read tool to verify visual appearance
5. For specific pages: `npx playwright screenshot http://localhost:5173/auctions/123 /tmp/auction-detail.png`
6. Full page capture: add `--full-page` flag

Use this workflow to validate:
- Dark theme is applied correctly
- Colors, spacing, and layout match design spec
- Responsive layouts at different viewport sizes
- Components render as intended

## Operational Notes

Succinct learnings about how to RUN the project: `[TODO]`

### Codebase Patterns

- Monorepo containing auction-service backend, frontend, and price-service backend
- auction-service is a Spring Boot Java application
- price-service is a Spring Boot Kotlin application
- frontend is a React + TypeScript application (Vite, Tailwind CSS, Vitest)
- gRPC for communication between auction-service and price-service
- Hexagonal / ports & adapters architecture with DDD principles
