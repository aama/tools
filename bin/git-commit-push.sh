#!/bin/bash
# usage:  git-commit-push.sh 

# 03/21/14: 

#. bin/constants.sh

#for d in `ls`
for d in afar beja-beniamer dahalo akkadian-ob beja-bishari dhaasanac alaaba beja-hadendowa dizi arabic bilin egyptian-middle arbore boni-jara elmolo awngi boni-kijee-bala gawwada bayso boni-kilii gedeo beja burji geez beja-arteiga burunge beja-atmaan coptic-sahidic 
do
	 echo "$d ********************************************"
	 cd $d
	 git add *.*
	 git commit -am "revised edn added with xml"
	 git push origin master
	 cd ../
done

