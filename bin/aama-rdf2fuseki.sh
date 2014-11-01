#!/bin/bash
# usage:  ~/aama $ tools/bin/aama-rdf2fuseki.sh "dir"

# 10/28/14

#. bin/constants.sh

echo "fuload.log" > logs/fuload.log;
 
#for d in afar geez

for d in `ls data`
do
    echo "$d ********************************************"
    fs=`find data/$d/ -name *.rdf`
    for f in $fs
    do
	l=${f%.rdf}
	lang=${l#data/}
	graph="http://oi.uchicago.edu/aama/2013/graph/`dirname ${lang/\/\///}`"
	echo posting $f to $graph;
	fuseki/jena-fuseki-1.1.1/s-post -v http://localhost:3030/aama/data $graph  $f 2>&1 >>logs/fuload.log
    done
done

# i.e.
#    ${FUSEKIDIR}/s-post -v http://localhost:3030/aama/data \
#                          http://oi.uchicago.edu/aama/2013/graph/beja-arteiga  \
#			  data/beja-arteiga//beja-arteiga-pdgms.rdf \
#			  2>&1 \ #"redirect stderr to stdout"
#			  >>logs/fuload.log
