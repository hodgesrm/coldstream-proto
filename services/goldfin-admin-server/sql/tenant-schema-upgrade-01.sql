-- This script should run using the service database owner. 

-- Set schema to tenant that will be upgraded. 
-- Run \set AUTOCOMMIT off if in psql. 

-- Start a nice transaction.
BEGIN;

-- Add fields to invoices. 
ALTER TABLE invoices ADD COLUMN account varchar(100)
;
ALTER TABLE invoices ADD COLUMN credit numeric(10,2)
;

-- Add fields to invoice_items and remove one_time_charge. 
ALTER TABLE invoice_items RENAME COLUMN item_row_number TO rid
;
ALTER TABLE invoice_items ADD COLUMN parent_rid integer
;
ALTER TABLE invoice_items ADD COLUMN item_row_type varchar(10) DEFAULT 'DETAIL'
;
ALTER TABLE invoice_items ADD COLUMN charge_type varchar(10) DEFAULT 'RECURRING'
;
ALTER TABLE invoice_items ADD COLUMN unit_type varchar(10)
;
ALTER TABLE invoice_items ADD COLUMN subtotal_amount numeric(10,2)
;
ALTER TABLE invoice_items ADD COLUMN credit numeric(10,2)
;
ALTER TABLE invoice_items ADD COLUMN tax numeric(10,2)
;
ALTER TABLE invoice_items DROP COLUMN one_time_charge
;

-- Commit if we got to this point.
COMMIT;
