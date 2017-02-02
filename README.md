# animalia

Welcome to animalia, Rafa's at-home interview question. The purpose of this exercise is for you to demonstrate your software development competency by implementing a web API that we will specify below. The animalia web API allows clients to add facts to the animalia service about animals, their characteristics and relationships and to then ask the animalia service questions about animals. The interaction with the service uses a JSON documents to describe facts or pose structured questions.

# Submitting your Implementation

We expect two deliverables in your submission.

1. The source code.
2. A deployed service OR thorough instructions for building and running the service on a modern Mac laptop.

Your job is to write an implementation that satisfies the API specification and verify your implementation. You may use any implementation language you prefer.

### Submitting source code

To submit the source code, the easiest way is to share a private Github or BitBucket repository with us (we will send you the appropriate usernames). Alternatively, we can accept compressed tarballs or zip archives. We cannot accept those over email, though, so we recommend a file sharing service like Google Drive, Dropbox, or similar.

### Deployed service

It is easiest for us to evaluate the implementation if you deploy it somewhere. If you haven't done this before, [AWS Lambda][lambda], [AWS Elastic Beanstalk][eb], [Heroku][heroku], [Google AppEngine][gae], [OpenShift][openshift], [Azure][azure], and similar services provide easy ways to deploy web services to the cloud often with free tiers. You can then send us a link to your deployed app.

If you are uncomfortable deploying your app, we will accept thorough instructions on how to setup and run your service as an alternative. We are experienced developers, but we may not be familiar with the tools or languages you used, so please draft the instructions accordingly.

[eb]: https://aws.amazon.com/elasticbeanstalk/
[lambda]: https://aws.amazon.com/lambda/
[heroku]: https://heroku.com/
[gae]: https://cloud.google.com/appengine/docs
[openshift]: https://www.openshift.com/
[azure]: https://azure.microsoft.com/en-us/

### External Services and Libraries

Feel free to use whatever external services and libraries you feel are best suited to solve the problem. It is not necessary to write the code for your solution from the ground up. You can use databases, utility libraries, or external web services as you see fit.

# Assessment and Interview

After we receive your submission we will conduct a code review and execute our suite of integration tests that assert the correctness of the API. Through the course of our review and testing we will assess your implementation on several different criteria:

* _Correctness:_ Does your API adhere to the specification and does it return correct or otherwise reasonable results through the course of training and querying?
* _Robustness:_ Does your implementation handle malformed, edge case, or fuzzed input without failing and while returning meaningful messages on the cause of the failure?
* _Readability:_ Can an engineer unfamiliar with your implementation read and understand what you wrote with sufficient depth to make modifications? This criteria speaks to style, naming conventions, organization, and comments.
* _Scalability:_ If we were to scale the training data from its current form to 10s of thousands of animal concepts will your implementation be able to support the larger number of concepts without become unusably slow or otherwise broken.

On the day of your on-site interview you will present your solution to 2-3 members of the engineering team. You should prepare to talk about your implementation approach, design trade offs and approach to testing and validation. We will also ask you to run your test suite.

Through the course of the one-on-one interviews we will ask you further questions about how you would extend your service implementation and how you would fix any issues we find in our own testing to improve your solution.

# Fact Data Specification

The fact data conforms to the following contraints:

## Semantic Relationships

There are a finite and defined set of semantic relationships, which are as follows:

* _has:_ The subject possesses the object.
* _eats:_ The subject eats the object for its food
* _lives:_ The subject inhabits a location
* _isa:_ The subject is sub class of the object (e.g. scale is a bodypart, leg is a bodypart, fish is a food, forest is a place)
 
We have provided example fact data for this project that comes in the form of a csv file. The columns may contain lists. The list items are separated by ':' characters. Columns may have zero or more values.

* _concept:_ The name of the concept. For example: 'otter', 'river', 'berry', 'canid', 'mammal'
* _isa:_ Concepts of which this concept is a subsclass
* _lives:_ Concepts that define where this concept lives
* _has:_ Concepts that this concept has
* _eats:_ Concepts that this concept eats

# API Specification

The API is a HTTP/REST API. The document format is JSON, with a Content Type of `application/json`.

## Fact API

You assert facts to your service by sending json documents to the service in a particular format. The fact documents have the abstract form of: `subject relationship object`. For example: "the otter lives in rivers". In that example the article the subject is "otter", the semantic relationship is "lives in", and the object is "rivers".

Training is done by submitting a HTTP POST to the to the /animals/facts resource with a JSON encoded body that contains a single fact in a JSON object according to the scheme specified below:

 `{
 "subject": "otter",
 "rel": "lives", 
 "object": "rivers"
}`

Where the "rel" field is drawn from, and limited to, the set of semantic relationships defined above. Both subject and object are drawn from the set of concepts and may introduce new concepts to the service if they are seen 
for the first time.    

If the POST completed successfully the API will return a HTTP 200 status code with a JSON formatted body that contains an identifier for the newly created fact. The response document will look like this:


 `{
 "id": "0b3431e3-2351-46f1-ad90-fa022a60ba15"
}`

The id is a globally unique identifier (GUID).

You can submit the exact same fact repeatedly and the service will be trained in the exact same way as a result and will return the exact same identifier.

Facts that cannot be parsed by the service because they do not follow the expected form will result in a HTTP 400 status code and a error message detailing that the parse attempt has failed. The error message will also be in a JSON format and will look as follows:

 `{
 "message": "Failed to parse your fact"
}`

## Fact Management API

Individual facts can be retrieved using an HTTP GET on the '/animals/facts/' resource using the GUID to specify the target fact:

`
GET /animals/facts/0b3431e3-2351-46f1-ad90-fa022a60ba15  HTTP/1.1
`

If a fact with the specified id is known to the service it is returned with a 200 status code and a response document that looks like this:

 `{
 "subject": "otter",
 "rel": "lives", 
 "object": "rivers"
}` 

If a fact with the specified id is not present then the response will have a 404 status code and response body will be empty.

Individual facts can also be deleted. To delete a fact the client can issue an HTTP DELETE and specify the GUID of the fact to delete like this:

`
DELETE /animals/facts/0b3431e3-2351-46f1-ad90-fa022a60ba15  HTTP/1.1
`

If a fact with the specified GUID exists then it will be deleted and the response will have a status code of 200 and have a response body with the id of the fact that was deleted. This response document should look as follows:

 `{
 "id": "0b3431e3-2351-46f1-ad90-fa022a60ba15"
}`

If a fact with the specified GUID could not be found then the service will return a response with a 404 status code and no response body.

When a fact is deleted then the information it represents about animals is no longer available to the service and the service must stop answering questions with information from the fact.

## Query API

You query the API about the semantic relationships of concepts. You can query either how many concepts meet the given semantic definition, or which concepts meet the semantic definition.  Each of the query types have their own resource and response type. 

A query is submitted by sending a HTTP GET to the /animals/which or the /animals/how-many resource with parameters  s', 'r', and 'o'. The meaning of the parameters are:

* _s:_ The subject of the query, drawn from the set of known concepts
* _r:_ The required semantic relationship, drawn from the set of semantic concepts above.
* _o:_ The object of the semantic relationship.

### /animals/which

Example request that expresses "which animals have legs?":

`
GET /animals/which?s="animals"&r='has'&o='legs' HTTP/1.1
`

The service will respond with a 200 status code and result. Results for "which" queries will be an array of strings:

`["otter","fox","moose"]
`

### /animals/how-many

Example request that expresses "how many animals have legs?":

`
GET /animals/how-many?s="animals"&r='has'&o='legs' HTTP/1.1
`

The service will respond with a 200 status code and result. Results for "how-many" queries is simply a number.

`3`

### Errors

If the API cannot find information that is related to the query, it will return a 404 status code and a slightly different JSON body:

`{
 "error": "I can't answer your query."
}`

If the request is malformed then the API will return a HTTP 400 status code.

# Example Queries

Your implementation will need to be able to handle queries that are simple lookups of attributes, aggregations of animals or counts with common features, and queries that require inference. Some example questions are:

* How many animals have fins?
* Which animals eat berries?
* Which animals eat mammals?
* Which bears have scales?
* How many mammals live in the ocean?
