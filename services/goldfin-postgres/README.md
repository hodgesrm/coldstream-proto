This is a placeholder for PostgreSQL-related files. 

For dev/test purposes we use docker postgres images.  Here's how to 
pull and run: 

```
# Download latest version. 
docker pull postgres
# Run detached image without a volume. 
#docker run --name postgres -e POSTGRES_PASSWORD=secret -d postgres
docker run -e POSTGRES_PASSWORD=secret -p 15432:5432 -d postgres

# Find out the postgres container network IP on docker bridged network.
docker network inspect bridge
# Connect to IP using psql.
psql -hlocalhost -Upostgres -p15432
```

Run PostgresSQL on docker with persistent volume. Image name is Postgres-10.
```
# Create the volume.
docker volume create postgres-10
# Start docker with that volume. 
docker run -e POSTGRES_PASSWORD=secret \
 -v postgresql-10:/var/lib/postgresql/data \
 --name=Postgres-10 -p 15432:5432 -d postgres
```

Stop the PostgreSQL DBMS gracefully. 
```
docker stop Postgres-10
```

Start the PostgreSQL DBMS again. 
```
docker start Postgres-10
```
