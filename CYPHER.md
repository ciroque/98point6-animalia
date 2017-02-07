# Neo4j Cypher notes

## MERGE

http://graphaware.com/neo4j/2014/07/31/cypher-merge-explained.html

### Otter
 MERGE (s1:Subject { name: "otter" }) MERGE (o1:Object { name:"leg" }) MERGE (s1)-[:HAS]-(o1) MERGE (s1)-[:HAS]->(o2:Object { name:"tail" });

 MERGE (s1:Subject { name: "otter" }) MERGE (o1:Object { name:"berries" }) MERGE (s1)-[:EATS]-(o1);
 MERGE (s1:Subject { name: "otter" }) MERGE (o1:Object { name:"herring" }) MERGE (s1)-[:EATS]-(o1);
 MERGE (s1:Subject { name: "otter" }) MERGE (o1:Object { name:"salmon" }) MERGE (s1)-[:EATS]-(o1);
 MERGE (s1:Subject { name: "otter" }) MERGE (o1:Object { name:"grass" }) MERGE (s1)-[:EATS]-(o1);

 MERGE (s1:Subject { name: "otter" }) MERGE (o1:Object { name:"forest" }) MERGE (s1)-[:LIVES]-(o1);
 MERGE (s1:Subject { name: "otter" }) MERGE (o1:Object { name:"meadow" }) MERGE (s1)-[:LIVES]-(o1);
 MERGE (s1:Subject { name: "otter" }) MERGE (o1:Object { name:"den" }) MERGE (s1)-[:LIVES]-(o1);

 MERGE (s1:Subject { name: "otter" }) MERGE (o1:Object { name:"mammal" }) MERGE (s1)-[:ISA]-(o1);
 MERGE (s1:Subject { name: "otter" }) MERGE (o1:Object { name:"animal" }) MERGE (s1)-[:ISA]-(o1);

### Deer
#### HAS
 MERGE (s1:Subject { name: "deer" }) MERGE (o1:Object { name:"leg" }) MERGE (s1)-[:HAS]-(o1);
 MERGE (s1:Subject { name: "deer" }) MERGE (o1:Object { name:"tail" }) MERGE (s1)-[:HAS]-(o1);

#### ISA
 MERGE (s1:Subject { name: "deer" }) MERGE (o1:Object { name:"mammal" }) MERGE (s1)-[:ISA]-(o1);
 MERGE (s1:Subject { name: "deer" }) MERGE (o1:Object { name:"animal" }) MERGE (s1)-[:ISA]-(o1);
 
 
 MERGE (s:Subject { name: "whale" }) 
 MERGE (o:Object { name:"tail" }) 
 MERGE (s)-[r:HAS]-(o)
 ON CREATE SET r.uuid = "c81e9f78-219d-42f1-9538-17df409d788e"
 RETURN r.uuid
 ;