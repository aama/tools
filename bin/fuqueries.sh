#!/bin/sh
# usage:  bin/fuqueries.sh

# 08/12/13: Run some standard queries
AAMA_BIN=tools/bin
	
${AAMA_BIN}/fuquery-gen.sh ${AAMA_BIN}/count-triples.rq
${AAMA_BIN}/fuquery-gen.sh ${AAMA_BIN}/list-graphs.rq

