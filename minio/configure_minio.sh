#!/bin/sh
mc alias set local ${MINIO_HOST:-"http://localhost:9000"} ${MINIO_ROOT_USER:-kot} ${MINIO_ROOT_PASSWORD:-87654321}
mc admin accesskey create local --access-key "Q3AM3UQ867SPQQA43P2F" --secret-key "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG"
mc mb local/avatars
mc anonymous set download local/avatars
mc mb local/content
mc anonymous set download local/content