#!/bin/sh

# rev 10/28/14
# Start the server before queries

 cd fuseki/jena-fuseki-1.1.1/
./fuseki-server  --config=../../tools/aamaconfig.ttl 
#./fuseki-server -v  --update --loc=aama /aamaData
