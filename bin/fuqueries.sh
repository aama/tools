#!/bin/sh
# usage:  bin/fuqueries.sh

# 08/12/13: Run some standard queries

	
tools/bin/fuquery-gen.sh tools/sparql/rq-ru/count-triples.rq
tools/bin/fuquery-gen.sh tools/sparql/rq-ru/list-graphs.rq

