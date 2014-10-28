#!/bin/bash
# usage:  git-commit-push-tools.sh 

# 03/21/14: 

#. bin/constants.sh

#for d in rendille saho shinassha sidaama somali-standard syriac tsamakko wolaytta yaaku yemsa
cd tools
for d in `ls`
do
	 echo "$d ********************************************"
	 cd $d
#	 git rm *.xml
	 git add *.*
#	 git commit -am "xml data file removed"
	 git commit -am "revised tools files added"
	 git push origin master
	 cd ../
done

