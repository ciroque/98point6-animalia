# Neo4j Cypher notes

**WARN: These are notes. A scratch pad of ideas and discovery. The actual code may vary greatly.**

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
 
## QUERIES

 {
    Subject,
    Relationship,
    Object
 }
 
### Which / How many animals have legs?
 MATCH (subject { name: 'animal'})-[:isa]-(target)-[relationship:has]-(o { name:'leg' }) RETURN DISTINCT target.name as animals;
 MATCH (subject { name: 'animal'})-[:isa]-(target)-[relationship:has]-(o { name:'leg' }) RETURN DISTINCT COUNT(target.name) as count;
 
### How many animals have fins?
 MATCH (subject { name: 'animal'})<-[:isa]->(target)-[relationship:has]->(object { name:'fin' }) RETURN DISTINCT target.name as animals;

### Which animals eat berries?
 MATCH (subject { name: 'animal'})<-[:isa]-(target)-[relationship:eats]-(object { name: 'berries' }) RETURN DISTINCT target.name as animals;
 
### Which animals eat mammals?
 MATCH (subject { name: 'animal' })<-[:isa]-(target)-[relationship:eats]->(food)<-[:isa]->(object { name: 'mammal'}) return DISTINCT target.name as animals ORDER BY animals;
 
### Which bears have scales?
 MATCH (subject { name: 'bear'})<-[relationship:has]->(object { name: 'scale' }) RETURN DISTINCT result.name as animals;
 
### How many mammals live in the ocean?
 MATCH (subject { name: 'mammal' })<-[:isa]-(target)<-[relationship:lives]->(object { name: 'ocean' }) RETURN DISTINCT target.name as animals;

MATCH (subject { name: '<subject>'})-[relationship: <relationship>]-(object { name: '<object>'})
RETURN DISTINCT subject.name
;

MATCH (subject { name: 'bear'})<-[relationship:has]->(object { name: 'scale' }) RETURN DISTINCT result.name as animals;
MATCH (subject { name: 'animal' })<-[:isa]-(target)-[relationship:eats]->(food)<-[:isa]->(object { name: 'mammal'}) return DISTINCT target.name as animals ORDER BY animals;


### Brute force

#### Direct
-- which animals live in the ocean
-- which bears have scales
MATCH (subject { name: 'bear'})<-[:has]->(object { name: 'leg'}) RETURN DISTINCT subject.name as animals;


#### Indirect 1
-- which animals have legs
-- which animals eat berries
MATCH (subject { name: '.' })
MATCH (object { name: '.' })
MATCH (subject)<-[:isa]->(animal)<-[r:.]->(object)
MATCH (animal)<-[:isa]->({ name: 'animal' })
RETURN animal.name as animals
ORDER BY animals

#### Indirect 2
-- which animals eat mammals
-- which fish eat arachnids

MATCH ()<-[:has]->(animal)-[:isa]->({name: 'animal'})
MATCH (subject { name: '.'})
MATCH (object { name: '.'})
MATCH (thing)-[:isa]->(object)
MATCH (subject)-[:isa]-(animal)-[:.]->(thing)
RETURN DISTINCT animal.name as animals
;


#### And the unholy mess that is the Union between the three queries

MATCH (subject: Fact { name: 'mammal'})<-[:lives]->(object: Fact { name: 'ocean'})
RETURN DISTINCT subject.name as animals
ORDER BY animals

UNION

MATCH (subject { name: 'mammal' })
MATCH (object { name: 'ocean' })
MATCH (subject)<-[:isa]->(animal)<-[r:lives]->(object)
MATCH (animal)<-[:isa]->({ name: 'animal' })
RETURN animal.name as animals
ORDER BY animals

UNION

MATCH ()<-[:has]->(animal: Fact)-[:isa]->({name: 'animal'})
MATCH (subject: Fact { name: 'mammal'})
MATCH (object: Fact { name: 'ocean'})
MATCH (thing)-[:isa]->(object)
MATCH (subject)-[:isa]-(animal)-[:lives]->(thing)
RETURN DISTINCT animal.name as animals
ORDER BY animals
;

### Generic

**Never got this fully fleshed out**

(SUBJECT {?})<-[?]->(TARGET)-[RELATIONSHIP]->()<-[?]->(OBJECT {?}) 
RETURN target;

MATCH ()<-[:has]->(animal)-[:isa]->({name: 'animal'})
RETURN DISTINCT animal.name as animals
;

MATCH (subject { name: '<subject>'})
RETURN DISTINCT subject.name
;

MATCH (object { name: '<object>'})
RETURN DISTINCT object.name
;

