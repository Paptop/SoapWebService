docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
docker rmi hospital:v2
docker rmi tautvis/service 
docker rmi ilju3280/hospital:v2
