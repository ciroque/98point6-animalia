#!/usr/bin/env bash

if [ "$EUID" -ne 0 ]
  then echo "Please run as root"
  exit
fi

wget --no-check-certificate -O - https://debian.neo4j.org/neotechnology.gpg.key | apt-key add -

echo 'deb http://debian.neo4j.org/repo stable/' >/etc/apt/sources.list.d/neo4j.list

apt update

apt install neo4j

