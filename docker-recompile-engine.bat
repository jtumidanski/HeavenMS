docker stop ms-engine
docker rm ms-engine
docker-compose -f docker-compose.yml build --no-cache engine
docker-compose -f docker-compose.yml up -d engine