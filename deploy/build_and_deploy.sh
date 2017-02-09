#!/usr/bin/env bash

PUBLIC_IP=54.218.241.162

pushd ..

sbt debian:packageBin

scp -i ~/.ssh/animalia.pem target/Animalia_1.0_all.deb ubuntu@$PUBLIC_IP:/tmp

popd
