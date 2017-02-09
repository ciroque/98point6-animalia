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
9) Install httpie: ```sudo apt install httpie```
10) Change the Neo4j password: ```http -a neo4j:neo4j POST http://localhost:7474/user/neo4j/password password=Password23```
11) (Optional) Allow remote connections to Neo4j: 
```
    sudo vim /etc/neo4j/neo4j.conf
    :s/#dbms.connector.http.listen_address=:7474/dbms.connector.http.listen_address=0.0.0.0:7474/g
    :x
    
    sudo systemctl restart neo4j
```
12) (Optional) Connect to Neo4j server web app: http://<public-ip>:7474/browser/

## Service Deployment

1) Use the ```deploy/build_and_deploy.sh``` script to build the native Ubuntu package and copy it to the EC2 instance.
2) ssh to host: ```ssh -i <your-key-file> ubuntu@<public-ip>```
3) switch to tmp directory: ```cd /tmp```
4) Install the package: ```sudo dpkg -i Animalia_1.0_all.deb``` 
5) Start the service: ```CLASSPATH=/usr/share/animalia/lib/* java org.ciroque.animalia.Main &``` // TODO: Make this a systemd
