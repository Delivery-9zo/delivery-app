#!/usr/bin/env bash
DEPLOY_PATH=/home/ec2-user/app/
DOCKER_COMPOSE_NAME=docker-compose.yml
docker compose -f $DEPLOY_PATH$DOCKER_COMPOSE_NAME up --build -d
