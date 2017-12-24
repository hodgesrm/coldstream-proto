// This script should run by the service database owner. 

// Create the invoice envelope table. 
CREATE TABLE IF NOT EXISTS invoice_envelopes (
  id uuid PRIMARY KEY, 
  description text, 
  tags text,
  state varchar(10) CHECK (state IN ('CREATED', 'SCANNED', 'INTERPRETED', 'ERROR')),
  source json, 
  ocrscan text,
  content json,
  vendor uuid, 
  identifier varchar(100), 
  effective_date timestamp, 
  subtotal numeric(12,2),
  tax numeric(12,2),
  total numeric(12,2), 
  currency varchar(10),
  creation_date timestamp DEFAULT current_timestamp
)
;

// Create the vendors table. 
CREATE TABLE IF NOT EXISTS vendors (
  id uuid PRIMARY KEY, 
  identifier varchar(250) UNIQUE, 
  name varchar(250) UNIQUE,
  state varchar(10) CHECK (state IN ('ACTIVE', 'INACTIVE')),
  creation_date timestamp DEFAULT current_timestamp
)
;
