# Nova — real-time space operations dashboard

A mission-control style dashboard: live ISS position, upcoming launches, near-Earth asteroids, space weather
and NASA's picture of the day on one screen, plus **Nova AI**, a chat assistant that answers questions using the
latest cached space data.

**Stack:** Spring Boot · Spring Security (JWT) · WebSocket/STOMP · Redis · MySQL · React (Vite) · Zustand · Tailwind · Claude API

## How it works

```
External APIs ──► @Scheduled jobs ──► Redis (TTL cache) ──► STOMP topics ──► React dashboard
(NASA, SpaceX,                                                /topic/iss, /topic/launches,
 Open Notify, ISRO,                                           /topic/neo, /topic/weather, /topic/brief
 The Space Devs)

React chat ──► /app/nova/chat ──► NovaAiService (Redis brief + Claude) ──► /user/{id}/queue/nova
```

ISS position refreshes every 5 s, launches and space weather every 5 min, asteroids hourly.

## Repository layout

| Path | What it is |
|---|---|
| [`nova-space-fullstack/nova/`](nova-space-fullstack/nova) | The app: `backend/` (Spring Boot) and `frontend/` (React). **Full setup guide in its [README](nova-space-fullstack/nova/README.md).** |
| `nova-fixes (1)/nova-fixes/` | Follow-up patch set (WebSocket config, Tailwind/PostCSS config) with its own `SETUP.md` |

## Quick start

Needs Java 17+, Node 20+, MySQL 8 and Redis running locally.

```bash
# backend
cd nova-space-fullstack/nova/backend
export NASA_API_KEY=...  ANTHROPIC_API_KEY=...  DB_PASSWORD=...  JWT_SECRET=...
mvn spring-boot:run            # http://localhost:8080, WebSocket at /ws

# frontend (new terminal)
cd nova-space-fullstack/nova/frontend
npm install && npm run dev
```

All secrets are read from environment variables (`application.yml`); none are committed.
