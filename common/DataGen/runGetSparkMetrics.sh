#!/bin/bash
#install maven local jar file
#mvn install:install-file -Dfile=/home/minli/maven_repo/wikixmlj-r43.jar -DgroupId=edu.jhu.nlp -DartifactId=wikixmlj -Dversion=1.0 -Dpackaging=jar -DlocalRepositoryPath=/home/minli/maven_repo/

#default maven local repo: ~/.m2/repository
#mvn install:install-file -Dfile=${HOME}/maven_repo/wikixmlj-r43.jar -DgroupId=edu.jhu.nlp -DartifactId=wikixmlj -Dversion=1.0 -Dpackaging=jar 
printUsage(){
	echo "$0 "
	exit 1
}

opt="metrics"
if [ $# -ge 1 ];then
	opt=$1
fi

[ $# -ge 1 ] && opt=$1

if [ $opt = "metrics" ];then
	java -classpath target/sparkbench-data-gen-project-1.0.jar:/home/minli/.m2/repository/com/googlecode/json-simple/json-simple/1.1/json-simple-1.1.jar src.main.java.GetSparkMetrics $2
elif [ $opt = "genAll" ]; then
	mkdir /tmp/app_log; for i in `ls app_log/*log`; do echo $i; ./runGetSparkMetrics.sh metrics $i > /tmp/$i ;done
else
	printUsage
fi
#dir=/mnt/nfs_dir/sperf/spark_app/common/DataGen

#java -classpath ${dir}/target/sparkbench-data-gen-project-1.0.jar:${HOME}/.m2/repository/com/googlecode/json-simple/json-simple/1.1/json-simple-1.1.jar:${HOME}/.m2/repository/edu/jhu/nlp/wikixmlj/1.0/wikixmlj-1.0.jar:${HOME}/maven_repo/bzip2.jar src.main.java.WikiParser $1 $2

exit 0

dir=/home/minli/.m2/repository
if [ `hostname` = "" ];then
dir=/home/minli/.m2/repository
elif [ `hostname` = "" ];then
else
	echo `hostname`
fi