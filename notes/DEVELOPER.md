# Developer Notes

## Exercising the service by hand

### curl commands:

 curl -X POST http://localhost:9806/animals/facts --header "Content-Type:application/json" --data '{ "subject": "salmon", "rel": "lives", "object": "rivers" }' 
 
 curl -X POST http://localhost:9806/animals/facts --header "Content-Type:application/json" --data '{ "subject": "otter", "rel": "lives", "object": "rivers" }