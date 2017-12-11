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

// Create the user table. 
CREATE TABLE IF NOT EXISTS user (
  id uuid PRIMARY KEY, 
  tenant_id uuid REFERENCES table(id),
  username varchar(250) UNIQUE, 
  password_hash varchar(250),
  algorithm char(15)
)
;

// Grant full privileges on schema contents to service database owner. 
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA admin TO {{serviceUser}}
;
