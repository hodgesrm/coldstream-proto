// This script must be run by the DBMS superuser. 
//!NON-TRANSACTIONAL

// Drop the service database.
DROP DATABASE IF EXISTS {{serviceDb}}
;

// Drop the service user.
DROP ROLE {{serviceUser}}
;
