# kafka
docker-compose up -d  
docker-compose down  
docker exec -it kafka kafka-topics.sh --bootstrap-server localhost:9092 --create --topic testTopic 
