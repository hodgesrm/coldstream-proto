// This script should run using the service database owner. 

// Create the document table. 
CREATE TABLE IF NOT EXISTS documents (
  id uuid PRIMARY KEY,
  name varchar(250),
  description varchar(250),
  content_type varchar(100),
  content_length bigint, 
  thumbprint varchar(100), 
  locator varchar(250),
  state varchar(20) CHECK (state IN ('CREATED', 'SCAN_REQUESTED', 'SCANNED', 'ERROR')),
  semantic_type varchar(10) CHECK (semantic_type IN ('INVOICE', 'UNKNOWN')),
  semantic_id uuid,
  tags jsonb,
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
  identifier varchar(100), 
  account varchar(100), 
  effective_date timestamp, 
  vendor varchar(100), 
  subtotal_amount numeric(10,2), 
  credit numeric(10,2), 
  tax numeric(10,2), 
  total_amount numeric(10,2),
  currency char(3),
  tags jsonb,
  creation_date timestamp DEFAULT current_timestamp
)
;

// Create the invoice items table. 
CREATE TABLE IF NOT EXISTS invoice_items (
  invoice_id uuid REFERENCES invoices(id) ON DELETE CASCADE NOT NULL,
  rid integer NOT NULL,
  parent_rid integer,
  item_row_type varchar(10) default 'DETAIL',
  item_id varchar(250), 
  resource_id varchar(250), 
  description varchar(1000), 
  charge_type varchar(10) default 'RECURRING',
  unit_amount numeric(10,2), 
  units integer, 
  unit_type varchar(10),
  subtotal_amount numeric(10,2), 
  credit numeric(10,2), 
  tax numeric(10,2), 
  total_amount numeric(10,2), 
  currency char(3),
  start_date timestamp, 
  end_date timestamp,
  region text,
  inventory_id varchar(250), 
  inventory_type varchar(50), 
  tags jsonb,
  creation_date timestamp DEFAULT current_timestamp,
  PRIMARY KEY (invoice_id, rid)
)
;

// Create the vendors table. 
CREATE TABLE IF NOT EXISTS vendors (
  id uuid PRIMARY KEY, 
  identifier varchar(250) UNIQUE, 
  name varchar(250) UNIQUE,
  state varchar(10) CHECK (state IN ('ACTIVE', 'INACTIVE')),
  tags jsonb,
  creation_date timestamp DEFAULT current_timestamp
)
;

// Create the data_series table. 
CREATE TABLE IF NOT EXISTS data_series (
  id uuid PRIMARY KEY,
  name varchar(250),
  description varchar(250),
  content_type varchar(100),
  content_length bigint, 
  thumbprint varchar(100), 
  locator varchar(250),
  state varchar(20) CHECK (state IN ('CREATED', 'PROCESS_REQUESTED', 'PROCESSED', 'ERROR')),
  format varchar(10) CHECK (format IN ('OBSERVATION', 'UNKNOWN')),
  tags jsonb,
  creation_date timestamp DEFAULT current_timestamp
)
;
CREATE INDEX ON data_series USING hash(thumbprint)
;

// Create the hosts table. 
CREATE TABLE IF NOT EXISTS hosts (
  id uuid PRIMARY KEY,
  host_id varchar(100) NOT NULL,
  resource_id varchar(100),
  effective_date timestamp NOT NULL, 
  vendor_identifier varchar(250) NOT NULL,
  data_series_id uuid REFERENCES data_series(id) ON DELETE CASCADE,
  host_type varchar(20) CHECK (host_type IN ('BARE_METAL', 'CLOUD', 'UNKNOWN')),
  host_model varchar(100), 
  region varchar(50), 
  zone varchar(50), 
  datacenter varchar(50), 
  cpu varchar(100), 
  socket_count integer, 
  core_count integer,
  thread_count integer,
  ram bigint,
  hdd bigint,
  ssd bigint,
  nic_count integer,
  network_traffic_limit bigint,
  backup_enabled boolean,
  tags jsonb
)
;
CREATE INDEX ON hosts(host_id)
;
CREATE INDEX ON hosts(resource_id)
;
CREATE INDEX ON hosts(effective_date)
;
