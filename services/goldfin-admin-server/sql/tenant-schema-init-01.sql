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
  state varchar(20) CHECK (state IN ('CREATED', 'SCAN_REQUESTED', 'SCANNED', 'ERROR')),
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
  document_id uuid REFERENCES documents(id) ON DELETE CASCADE,
  description varchar(250),
  tags varchar(500),
  identifier varchar(100), 
  effective_date timestamp, 
  vendor varchar(100), 
  subtotal_amount numeric(10,2), 
  tax numeric(10,2), 
  total_amount numeric(10,2),
  currency char(3),
  creation_date timestamp DEFAULT current_timestamp
)
;

// Create the invoice items table. 
CREATE TABLE IF NOT EXISTS invoice_items (
  invoice_id uuid REFERENCES invoices(id) ON DELETE CASCADE NOT NULL,
  item_row_number integer NOT NULL,
  item_id varchar(250), 
  resource_id varchar(250), 
  unit_amount numeric(10,2), 
  units integer, 
  total_amount numeric(10,2), 
  currency char(3),
  start_date timestamp, 
  end_date timestamp,
  region text,
  inventory_id uuid, 
  inventory_type varchar(50), 
  creation_date timestamp DEFAULT current_timestamp,
  PRIMARY KEY (invoice_id, item_row_number)
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
