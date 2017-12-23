// This script must must be run by the DBMS superuser. 
//!NON-TRANSACTIONAL

// Create service user. 
CREATE ROLE {{serviceUser}} 
  WITH LOGIN CREATEDB CREATEROLE 
  PASSWORD '{{servicePassword}}'
;

// Create service database. 
CREATE DATABASE {{serviceDb}} WITH
  OWNER = {{serviceUser}}
  ENCODING = 'UTF8'
;
