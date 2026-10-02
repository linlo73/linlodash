#!/usr/bin/env bash
# Deploy/aggiornamento di applicazione1 su questo host, senza toccare le altre applicazioni.
# Uso: ./deploy/deploy-applicazione1.sh
set -euo pipefail
cd "$(dirname "$0")/.."

if [ ! -f applicazione1/.env ]; then
  echo "Manca applicazione1/.env — copialo da applicazione1/.env.example e personalizzalo." >&2
  exit 1
fi

docker compose -f docker-compose.applicazione1.yml pull --ignore-pull-failures postgres-app1
docker compose -f docker-compose.applicazione1.yml build applicazione1
docker compose -f docker-compose.applicazione1.yml up -d
docker image prune -f --filter "label=stage=build" >/dev/null 2>&1 || true

echo "applicazione1 deployata. Log: docker compose -f docker-compose.applicazione1.yml logs -f applicazione1"
