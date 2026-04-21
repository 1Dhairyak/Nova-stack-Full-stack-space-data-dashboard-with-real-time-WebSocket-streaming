# Nova — Local Development Setup Guide

## Prerequisites

| Tool | Required Version | Check | Install |
|------|-----------------|-------|---------|
| Java | 17 or 21 | `java -version` | https://adoptium.net |
| Maven | 3.9+ (via mvnw) | auto-downloaded | included in repo |
| Node.js | 20+ | `node -v` | https://nodejs.org |
| MySQL | 8+ | `mysql --version` | https://dev.mysql.com/downloads |
| Redis | 7+ | `redis-cli ping` | https://redis.io/docs/install |

---

## Step 1 — Clone and set up environment

```bash
git clone https://github.com/1Dhairyak/Nova-stack-Full-stack-space-data-dashboard-with-real-time-WebSocket-streaming.git nova
cd nova

# Copy and edit the env file
cp .env.example .env
# Fill in your values (see .env.example for comments on each key)
```

**Minimum required keys to get the app running:**
- `DB_PASSWORD` — your MySQL root password
- `NASA_API_KEY` — free at https://api.nasa.gov (or leave as `DEMO_KEY` for limited testing)
- `ANTHROPIC_API_KEY` — needed only for Nova AI chat; the dashboard works without it

---

## Step 2 — Start MySQL and Redis

### macOS (Homebrew)
```bash
brew services start mysql
brew services start redis
```

### Ubuntu / WSL
```bash
sudo service mysql start
sudo service redis-server start
```

### Windows
Start MySQL and Redis from their respective Windows services or installers.

### Verify both are running
```bash
mysql -u root -p -e "SELECT 1;"   # should print 1
redis-cli ping                      # should print PONG
```

---

## Step 3 — Run the backend

```bash
cd backend

# Export your environment variables (or source from .env)
export NASA_API_KEY=your_nasa_key
export ANTHROPIC_API_KEY=your_anthropic_key
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=any-random-32-char-string-here

# Start Spring Boot
./mvnw spring-boot:run
```

**First run:** Maven Wrapper downloads Maven 3.9.6 (~10MB) and all dependencies (~200MB).
This takes 2–5 minutes. Subsequent runs start in under 10 seconds.

**You should see:**
```
...Tomcat started on port 8080
...Started NovaApplication in X.XXX seconds
...ISS update pushed: XX.XX, XX.XX     ← scheduler started
```

### Windows alternative (no chmod needed)
```cmd
mvnw.cmd spring-boot:run
```

---

## Step 4 — Run the frontend

```bash
# In a new terminal tab
cd frontend
npm install        # first time only
npm run dev
```

Open **http://localhost:5173**

---

## Common Errors and Fixes

### `./mvnw: Permission denied`
```bash
chmod +x ./mvnw
```

### `Access denied for user 'root'@'localhost'`
Your `DB_PASSWORD` env var doesn't match your MySQL root password.
Reset MySQL root password:
```bash
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'newpassword';"
```

### `Unable to connect to Redis`
Redis isn't running. Start it:
```bash
# macOS
brew services start redis

# Linux
sudo systemctl start redis
```

### `ANTHROPIC_API_KEY not set` or Nova AI chat returns "Uplink lost"
Add your key to the environment. The dashboard works without it — only Nova AI chat needs it.

### `WebSocket connecting…` spinner never goes away
- Check the backend is running on port 8080
- Check `vite.config.js` proxy is set to `http://localhost:8080`
- Check browser console for WebSocket errors
- Make sure you're logged in — the WebSocket requires a JWT token

### `DEMO_KEY` NASA errors / rate limit
The DEMO_KEY is shared and hits rate limits quickly. Get a free personal key at https://api.nasa.gov — it takes 30 seconds and gives you 1000 requests/hour.

### `The Space Devs` returns 429 Too Many Requests
You've hit the 15 req/hr anonymous limit. Either get a free key at https://thespacedevs.com/api or just wait — the scheduler caches results and won't re-hit the API until TTL expires (5 minutes).

---

## API Keys Reference

| Service | Where to get it | Free tier |
|---------|----------------|-----------|
| NASA | https://api.nasa.gov | 1000 req/hr |
| Anthropic | https://console.anthropic.com | Pay per token |
| The Space Devs | https://thespacedevs.com/api | 15 req/hr |
| SpaceX | No key needed | Unlimited |
| ISRO Community | No key needed | Unlimited |
| Open Notify (ISS) | No key needed | Unlimited |
| NOAA (Kp index) | No key needed | Unlimited |

---

## Ports used

| Service | Port |
|---------|------|
| Spring Boot backend | 8080 |
| Vite frontend dev server | 5173 |
| MySQL | 3306 |
| Redis | 6379 |

Make sure nothing else is running on 8080 or 5173.

---

## Project structure recap

```
nova/
├── .env.example          ← copy to .env and fill in
├── SETUP.md              ← this file
├── backend/
│   ├── mvnw              ← Maven Wrapper (run this instead of `mvn`)
│   ├── .mvn/wrapper/     ← wrapper config (downloads Maven automatically)
│   ├── pom.xml
│   └── src/main/
│       ├── resources/application.yml   ← all config (reads from env vars)
│       └── java/com/nova/
└── frontend/
    ├── package.json
    ├── vite.config.js    ← proxies /api and /ws to localhost:8080
    ├── tailwind.config.js
    ├── postcss.config.js
    └── src/
```
