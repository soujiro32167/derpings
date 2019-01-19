#!/usr/bin/env bash

spark-submit \
 --deploy-mode cluster \
 --master spark://localhost:6066 \
 --driver-java-options "-Dquoted=value" \
 --class ca.eli.ClusterApp \
 /app/app.jar