#!/bin/bash
# usage:  ~/aama $ tools/bin/aama-edn2ttl.sh "dir"

# 10/28/14

#. bin/constants.sh

 
#for d in burji dizi  hebrew kemant saho yaaku

for d in `ls`
do
    echo "$d ********************************************"
    fs=`find $d/ -name *edn`
    for f in $fs
	do
		echo "generating ${f%\.edn}.ttl  from  $f "
		java -jar jar/aama-edn2ttl.jar $f > ${f%\.edn}.ttl
		echo "generating ${f%\.edn}.rdf  from  ${f%\.edn}.ttl "
		java -jar jar/rdf2rdf-1.0.1-2.3.1.jar \
		               ${f%\.edn}.ttl \
			       ${f%.edn}.rdf
	done
done
