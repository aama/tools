#!/bin/bash
# usage:  git-commit-push.sh 

# 03/21/14: 

#. bin/constants.sh

#for d in bilin kemant khamtanga
for d in `ls`
do
	 echo "$d ********************************************"
	 cd $d
	 git add *.*
	 git commit -am "revised edn added (after edn2ttl in aama-data)"
	 git push origin master
	 cd ../
done

