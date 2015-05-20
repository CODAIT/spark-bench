version 1.1

support spark 1.3.0

configuration: 
	setup: master address
compile:
	cd to each application:
	sbt package
	mvn package

execute: 
	bin/gen_data.sh
	bin/run.sh
