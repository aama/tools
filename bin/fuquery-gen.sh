#!/bin/bash
# usage:  fuquery-gen <qry> 

# example:
#    <aama> $ bin/fuquery-gen.sh sparql/rq-ru/count-triples.rq

echo "Query:" $1

fuseki/jena-fuseki-1.1.1/s-query --output=tsv --service http://localhost:3030/aama/query --query=$1 


#./s-query \
#	--output=tsv  \
#	--service http://localhost:3030/aamaTestData/query  \
#	--file=query-temp.rq  \
#	> ../cygwin/home/Gene/aamadata/tools/rq-ru/query-trial/$response
