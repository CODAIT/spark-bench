#!/bin/sh
echo "========== running LinearRegression benchmark =========="
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`

#mode="inputsize"
mode="partition"
#mode="mem"
#mode="mem_par"
#mode="feature"

if [ $mode = "partition" ]; then
	#vals="360 480 540  720   960  180  420 600 840 1080"; 				
	#vals="200 220 240 260 280 300 320 340"; 
	
	sed -i "/memoryFraction=/ c memoryFraction=0.5" bin/config.sh; 
	sed -i "/NUM_OF_FEATURES=/ c NUM_OF_FEATURES=6000" bin/config.sh;
	sed -i "/NUM_OF_EXAMPLES=/ c NUM_OF_EXAMPLES=8000" bin/config.sh;				
	vals="70 80 90 100 110 120 130 140 160 180 200 240"; 				
	for n in $vals; do 					
		sed -i "/NUM_OF_PARTITIONS=/ c NUM_OF_PARTITIONS=$n" bin/config.sh; 
		bin/gen_data.sh; 
		bin/run.sh; 
	done	
elif [ $mode = "mem" ]; then
	bin/gen_data.sh; 
	vals="0.15 0.1 0.05"
	for n in $vals; do 	
		echo "memoryfraction $n"
		sed -i "/memoryFraction=/ c memoryFraction=$n" bin/config.sh; 
		bin/run.sh; 
	done
elif [ $mode = "feature" ]; then	
	#vals="0.15 0.1 0.05"
	# 70M =data of4G
	n="0.5"
	sed -i "/memoryFraction=/ c memoryFraction=$n" bin/config.sh; 
	n="8000"	
	sed -i "/NUM_OF_EXAMPLES=/ c NUM_OF_EXAMPLES=$n" bin/config.sh;
	n="10"
	sed -i "/NUM_OF_PARTITIONS=/ c NUM_OF_PARTITIONS=$n" bin/config.sh; 
	vals="6000"
	for n in $vals; do 	
		echo "number of features $n"
		sed -i "/NUM_OF_FEATURES=/ c NUM_OF_FEATURES=$n" bin/config.sh;
		sed -i "/memoryFraction=/ c memoryFraction=0.1" bin/config.sh; 
		bin/gen_data.sh;
		sed -i "/memoryFraction=/ c memoryFraction=0.5" bin/config.sh; 
		bin/run.sh; 		
	done	
elif [ $mode = "mem_par" ]; then
	#pars="200 220 240 260 280 300 320 340"; 
	#for p in $pars; do
	n="600000000"
	sed -i "/NUM_OF_EXAMPLES=/ c NUM_OF_EXAMPLES=$n" bin/config.sh;
	for((p=800;p<=2100;p+=80)); do
		sed -i "/NUM_OF_PARTITIONS=/ c NUM_OF_PARTITIONS=$p" bin/config.sh; 		
		va="0.5 0.4 0.2";
		for n in $va; do 	
			echo "memoryfraction $n"
			sed -i "/memoryFraction=/ c memoryFraction=$n" bin/config.sh; 
			bin/gen_data.sh; 
			bin/run.sh; 
		done	
	done	
elif [ $mode = "inputsize" ]; then	
	#vals="0.15 0.1 0.05"
	# 70M =data of4G
	vals="70000000"
	for n in $vals; do 	
		echo "number of example $n"
		sed -i "/NUM_OF_EXAMPLES=/ c NUM_OF_EXAMPLES=$n" bin/config.sh;
		bin/gen_data.sh; 				
		bin/run.sh;
	done
else 
	echo "error!";	
fi

exit 0;