#!/usr/bin/env bash

if [ "$EUID" -ne 0 ]
  then echo "Please run as root"
  exit
fi

add-apt-repository ppa:webupd8team/java
apt-get update

apt-get install oracle-java8-installer
