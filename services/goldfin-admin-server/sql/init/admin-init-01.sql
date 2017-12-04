// This script should run by the service database owner. 

// Create the admin schema which holds tenant tables.
CREATE SCHEMA IF NOT EXISTS admin
;

// Create the tenant table. 
CREATE TABLE IF NOT EXISTS tenant (
  id uuid PRIMARY KEY, 
  name varchar(100) UNIQUE, 
  description text, 
  state varchar(10) CHECK (state IN ('PENDING', 'ENABLED', 'DISABLED')),
  creation_date timestamp DEFAULT current_timestamp
)
;

// Grant full privileges on schema contents to service database owner. 
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA admin TO {{serviceUser}}
;
