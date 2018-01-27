// This script should run using the service database owner. 

// Create the document table. 
CREATE TABLE IF NOT EXISTS documents (
  id uuid PRIMARY KEY,
  name varchar(250),
  description varchar(250),
  tags varchar(500),
  content_type varchar(100),
  content_length bigint, 
  thumbprint varchar(100), 
  locator varchar(250),
  state varchar(10) CHECK (state IN ('CREATED', 'SCANNED', 'ERROR')),
  semantic_type varchar(10) CHECK (semantic_type IN ('INVOICE', 'UNKNOWN')),
  semantic_id uuid,
  creation_date timestamp DEFAULT current_timestamp
)
;
CREATE INDEX ON documents USING hash(thumbprint)
;

// Create the invoice table. 
CREATE TABLE IF NOT EXISTS invoices (
  id uuid PRIMARY KEY, 
  document_id uuid,
  description varchar(250),
  tags varchar(500),
  identifier varchar(100), 
  effective_date date, 
  vendor varchar(100), 
  subtotal_amount numeric(10,2), 
  tax numeric(10,2), 
  total_amount numeric(10,2),
  currency char(3),
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
