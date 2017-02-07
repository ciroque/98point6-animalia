# Animalia Install Instructions

## Hosting

Decide where to host. Preference is to an EC2 instance running Ubuntu 16.04 server. Create the instance and use the following steps:

1) Copy Java install script to host: ```scp -i <your-key-file> install-oracle-java.sh ubuntu@<public-ip>:/tmp```
2) Copy Neo4j install script to host: ```scp -i <your-key-file> install-neo4j.sh ubuntu@<public-ip>:/tmp```
3) ssh to host: ```ssh -i <your-key-file> ubuntu@<public-ip>```
4) Switch to tmp directory: ```cd /tmp```
5) Allow execution of Java installation script: ```chmod +x install-oracle-java.sh```
6) Allow execution of Neo4j installation script: ```chmod +x install-neo4j.sh```
7) Install Java: ```sudo ./install-oracle-java.sh``` (Note that you will need to accept the license agreement!)
8) Install Neo4j: ```sudo ./install-neo4j.sh```

