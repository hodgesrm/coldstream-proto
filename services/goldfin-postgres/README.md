This is a placeholder for PostgreSQL-related files. 

For dev/test purposes we use docker postgres images.  Here's how to 
pull and run: 

  # Download latest version. 
  docker pull postgres
  # Run detached image. 
  docker run --name postgres -e POSTGRES_PASSWORD=secret -d postgres
  # Find out the postgres container network IP on docker bridged network.
  docker network inspect bridge
  # Connect to IP using psql.
  psql -h 172.17.0.2 -Upostgres 
