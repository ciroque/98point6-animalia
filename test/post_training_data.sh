#!/usr/bin/env bash

while read l
do
    echo curl -X POST --data \'${l}\' \"http://54.218.241.162:9806/animals/facts\" --header \"Content-Type:application/json\"
    ## "$(curl -s "$url/$i")"
done
