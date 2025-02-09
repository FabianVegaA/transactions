#!/bin/bash
# Get environment variables from .env file
POSTGRES_DB=$(grep POSTGRES_DB .env | xargs)
POSTGRES_USER=$(grep POSTGRES_USER .env | xargs)
POSTGRES_PASSWORD=$(grep POSTGRES_PASSWORD .env | xargs)

printf "POSTGRES_DB: $POSTGRES_DB\n"
printf "POSTGRES_USER: $POSTGRES_USER\n"

set -ex
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER docker;
    CREATE DATABASE $POSTGRES_DB;
    GRANT ALL PRIVILEGES ON DATABASE $POSTGRES_DB TO docker;
EOSQL
