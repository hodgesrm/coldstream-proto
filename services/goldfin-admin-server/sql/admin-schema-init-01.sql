// This script should run by the service database owner. 

// Create the tenant table. 
CREATE TABLE IF NOT EXISTS tenants (
  id uuid PRIMARY KEY, 
  name varchar(100) UNIQUE, 
  description text, 
  state varchar(10) CHECK (state IN ('PENDING', 'ENABLED', 'DISABLED')),
  creation_date timestamp DEFAULT current_timestamp
)
;

// Create the user table. 
CREATE TABLE IF NOT EXISTS users (
  id uuid PRIMARY KEY, 
  tenant_id uuid REFERENCES tenants(id) ON DELETE CASCADE,
  username varchar(250) UNIQUE, 
  roles varchar(250), 
  password_hash varchar(250),
  algorithm char(15),
  creation_date timestamp DEFAULT current_timestamp
)
;

// Create the sessions table. 
CREATE TABLE IF NOT EXISTS sessions (
  id uuid PRIMARY KEY, 
  user_id uuid REFERENCES users(id) ON DELETE CASCADE NOT NULL,
  token varchar(250), 
  last_touched_date timestamp DEFAULT current_timestamp, 
  creation_date timestamp DEFAULT current_timestamp
)
;

// Grant full privileges on schema contents to service database owner. 
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA {{serviceSchema}} TO {{serviceUser}}
;