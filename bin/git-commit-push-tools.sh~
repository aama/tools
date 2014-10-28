#!/bin/bash
# usage:  git-commit-push.sh 

# 03/21/14: 

#. bin/constants.sh

#for d in rendille saho shinassha sidaama somali-standard syriac tsamakko wolaytta yaaku yemsa
for d in `ls`
do
	 echo "$d ********************************************"
	 cd $d
#	 git rm *.xml
	 git add *.edn
#	 git commit -am "xml data file removed"
	 git commit -am "revised edn data file added"
	 git push origin master
	 cd ../
done

