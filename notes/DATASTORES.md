## Datastore Selection

### Neo4j

#### Overview

Neo4j is a Graph database which maps very well to the Animalia problem space. 

#### Model

- Two different Node types (Subject and Object) 
- Four Relationship types (has, eats, lives, isa).

#### Pros

- Natural modeling of problem space.
- Maps easily to request / response format.

#### Cons

- Not widely used, somewhat niche.

### MongoDB

#### Overview

MongoDB is a document database which works naturally and very well with JSON documents which aligns well with the requirements for the data interface with the service. A document 

#### Model

Document-based:

- Option 1, document per fact:
```
    {
        "_id": "975fe21c-f4f9-4079-8ef5-de77d86c7079",
        "subject": "...",
        "rel": "has|eats|lives|isa",
        "object": "..."
    }
```

- Option 2, document per subject:

```
    {
        "_id": "975fe21c-f4f9-4079-8ef5-de77d86c7079",
        "subject": "...",
        "facts": [
        {
            "rel": "rel": "has|eats|lives|isa",
            "object": ""
        }
        ]
    }
```

#### Pros

- Maps directly to request / response formats of service.
- Good community support and adoption.

#### Cons

- Data model is not strictly hierarchical but can be mapped as such.

### PostgreSQL

#### Overview

PostgreSQL is a standard relational database system.

#### Model

Normalized tables:

```
    Subjects(Id, Name)
    Objects(Id, Name)
    Relationships(Id, Name, SubjectId, ObjectId)
```

#### Pros

- Easy to model the problem space.
- Well known, widely adopted, broad community support.

#### Cons

- Maps to request / response format through ORM which can add time and cognitive overhead.