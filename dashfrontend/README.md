# Dash Frontend — Angular monorepo

Angular 22 workspace with:
- `auth`: reusable Angular authentication library
- `auth-demo`: small application showing the auth library
- `shopping`: shopping-list application
- `i18n`: centralized translation messages

## Install

```bash
npm install
```

## Build

```bash
npm run build:auth
npm run build
```

Angular applications consume the built local library through the workspace path mapping.

## Run

```bash
npm run start:shopping
```

or:

```bash
npm run start:auth
```

The shopping app loads its API endpoint from `projects/shopping/public/config.json`
at runtime. Update `apiUrl` there; it should point to the backend API root, such
as `http://localhost:8082/api`. A trailing slash is accepted.

The auth library currently expects:
- POST `/auth/login`
- POST `/auth/register`
- GET `/auth/me`
- POST `/auth/logout`

The library uses `HttpOnly` cookie-friendly requests (`withCredentials: true`).


## Intended repository layout

```text
dashfrontend/
├── projects/
│   ├── auth/          # reusable library
│   ├── auth-demo/     # authentication application/demo
│   └── shopping/      # shopping application
├── i18n/              # centralized messages for all apps
├── angular.json
├── package.json
└── tsconfig.json
```

The backend remains outside this workspace in your existing `dash-backend/`, with its reusable `auth` module alongside the domain modules.
