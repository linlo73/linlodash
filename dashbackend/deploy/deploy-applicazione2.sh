#!/usr/bin/env bash
# Deploy/aggiornamento di shopping su questo host, senza toccare le altre applicazioni.
# Uso: ./deploy/deploy-shopping.sh
set -euo pipefail
cd "$(dirname "$0")/.."

if [ ! -f shopping/.env ]; then
  echo "Manca shopping/.env — copialo da shopping/.env.example e personalizzalo." >&2
  exit 1
fi

docker compose -f docker-compose.shopping.yml pull --ignore-pull-failures postgres-app2
docker compose -f docker-compose.shopping.yml build shopping
docker compose -f docker-compose.shopping.yml up -d
docker image prune -f --filter "label=stage=build" >/dev/null 2>&1 || true

echo "shopping deployata. Log: docker compose -f docker-compose.shopping.yml logs -f shopping"
