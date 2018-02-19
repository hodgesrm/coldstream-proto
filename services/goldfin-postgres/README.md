This is a placeholder for PostgreSQL-related files. 

For dev/test purposes we use docker postgres images.  Here's how to 
pull and run: 

  # Download latest version. 
  docker pull postgres
  # Run detached image. 
  #docker run --name postgres -e POSTGRES_PASSWORD=secret -d postgres
  docker run -e POSTGRES_PASSWORD=secret -p 15432:5432 -d postgres

  # Find out the postgres container network IP on docker bridged network.
  docker network inspect bridge
  # Connect to IP using psql.
  psql -hlocalhost -Upostgres -p15432

