#!/bin/bash
set -ex

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER docker;
    CREATE DATABASE tenpo;
    GRANT ALL PRIVILEGES ON DATABASE tenpo TO docker;
    CREATE DATABASE tenpo_test;
    GRANT ALL PRIVILEGES ON DATABASE tenpo_test TO docker;
EOSQL
