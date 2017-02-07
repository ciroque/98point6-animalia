## General

- Return a 500 for database / service errors? 
- What level of detail should be exposed in the above case? My preference would be to provide a correlation id to a log entry.


## FactAPI

- Just confirming, all errors (malformed JSON, invalid relationships, missing fields) result in the same error message back to the user ("Failed to parse your fact"), correct?

