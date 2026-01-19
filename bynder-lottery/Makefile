.PHONY: up down infra-up infra-down test

# Build and start all services + infrastructure (db, rabbitmq)
up:
	./ops/mvnw clean install -DskipTests
	docker-compose -f ops/docker-compose.full.yml up -d --build

# Stop and remove all containers and volumes
down:
	docker-compose -f ops/docker-compose.full.yml down -v

# Start only infrastructure (db, rabbitmq) for local development
infra-up:
	docker-compose -f ops/docker-compose.yml up -d

# Stop and remove all containers and volumes
infra-down:
	docker-compose -f ops/docker-compose.yml down -v

# Run tests
test:
	./ops/mvnw test
