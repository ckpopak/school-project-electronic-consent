# School Project — Electronic Consent (Demo)

Fictional demo of an electronic consent workflow (parent photo consent for school promotions). Not affiliated with DH, CHP, or eHealth.

## Stack

- **Frontend:** React (Vite) — http://localhost:5173
- **Backend:** Spring Boot 3 + Java 17 — http://localhost:8080
- **Database:** H2 in-memory (Flyway seeds on startup)

## Run locally

```bash
# Terminal 1 — API
cd backend
mvn spring-boot:run

# Terminal 2 — web UI
cd frontend
npm install
npm run dev
```

Open http://localhost:5173

### Demo accounts (password for all: `demo123`)

| Role   | Email                   | Notes |
|--------|-------------------------|-------|
| Parent | parent1@demo.local      | Chris & Sam (Riverside) |
| Parent | parent2@demo.local      | Taylor, Jamie, Parker |
| Parent | parent3@demo.local      | Avery & Quinn (KG) |
| Parent | parent4@demo.local      | Blake & Sky |
| Parent | parent5@demo.local      | River (secondary) |
| School | school@riverside.demo   | Riverside Primary |
| School | school@harbour.demo     | Harbour View KG |
| School | school@summit.demo      | Summit Secondary |

Sample campaign links: `/c/riverside-photo-2027`, `/c/harbour-openday-2027`, `/c/summit-grad-2027`, `/c/riverside-closed-demo` (expired).

## Deployment

| Layer | Host | URL |
|-------|------|-----|
| Frontend | Netlify | https://school-project-electronic-consent.netlify.app |
| API | Render (Docker) | https://school-project-electronic-consent.onrender.com |

Netlify proxies `/api/*` to Render (see `frontend/public/_redirects` and `netlify.toml`).

**Render (one-time):** In [Render Dashboard](https://dashboard.render.com/), **New → Blueprint**, connect `ckpopak/school-project-electronic-consent`, and apply `render.yaml`. Set `APP_CORS_ORIGINS` to include your Netlify URL if you change the site name.

**Netlify admin:** https://app.netlify.com/projects/school-project-electronic-consent
