#!/bin/bash
# usage:  git-pull.sh 
# to pull in changes in origin repo not yet in dev machine
# 03/21/14: 

#. bin/constants.sh

#for d in `ls`
for d in hebrew shinassha iraqw sidaama kambaata somali-standard kemant syriac khamtanga tsamakko koorete wolaytta maale yaaku oromo yemsa rendille hadiyya saho
do
	 echo "$d ********************************************"
	 cd $d
	 git pull
	 cd ../
done

