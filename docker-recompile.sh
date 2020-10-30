docker stop ms-$1
docker rm ms-$1
docker-compose -f docker-compose.yml build --no-cache $1
docker-compose -f docker-compose.yml up -d $1