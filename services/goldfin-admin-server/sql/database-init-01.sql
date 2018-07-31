// This script must must be run by the DBMS superuser. 
//!NON-TRANSACTIONAL

// Create service user. 
CREATE ROLE {{serviceUser}} 
  WITH LOGIN CREATEDB CREATEROLE 
  PASSWORD '{{servicePassword}}'
;

// Grant this role to current user.  Necessary for RDS PostgreSQL
// or CREATE DATABASE will fail.  (See 
// https://stackoverflow.com/questions/26684643/error-must-be-member-of-role-when-creating-schema-in-postgresql). 
GRANT {{serviceUser}} TO CURRENT_USER
;

// Create service database. 
CREATE DATABASE {{serviceDb}} WITH
  OWNER = {{serviceUser}}
  ENCODING = 'UTF8'
;
