How to run the generated docker image. 

Look for image using docker images.  You'll see something like: 

REPOSITORY                  TAG                 IMAGE ID            CREATED             SIZE
goldfin/rest-api            0.0.1               e9b2532f7d18        8 seconds ago       645MB

Start the docker image using the IMAGE ID tag. 

docker -l debug run -it -p 7080:8080 e9b2532f7d18

How to login: 

curl -d '{"user":"x", "password":"y"}' -H "Content-Type: application/json" \
-X POST http://localhost:8080/api/v1/login -v
