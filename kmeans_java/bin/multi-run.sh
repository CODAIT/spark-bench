#!/bin/sh
echo "========== running ${APP} benchmark =========="
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`

mode="inputsize"
#mode="partition"
#mode="mem"
#mode="mem_par"

if [ $mode = "partition" ]; then
	n="0.5"
	sed -i "/memoryFraction=/ c memoryFraction=$n" bin/config.sh; 
	n="210000000"
	sed -i "/NUM_OF_EXAMPLES=/ c NUM_OF_EXAMPLES=$n" bin/config.sh;
	#vals="360 480 540  720   960  180  420 600 840 1080"; 				
	vals="200 220 240 260 280 300 320 340"; 				
	#vals="60 "; 				
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
elif [ $mode = "mem_par" ]; then
	#pars="200 220 240 260 280 300 320 340"; 
	#for p in $pars; do
	n="210000000"
	sed -i "/NUM_OF_EXAMPLES=/ c NUM_OF_EXAMPLES=$n" bin/config.sh;
	for((p=220;p<=2100;p+=80)); do
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
	#vals="10000000 50000000 100000000 100000000 500000000 "
	vals="10000000 50000000 100000000 150000000"
	for n in $vals; do 	
		echo "number of points $n"
		sed -i "/NUM_OF_POINTS=/ c NUM_OF_POINTS=$n" bin/config.sh;
		bin/gen_data.sh; 	
		bin/run.sh;
	done
else 
	echo "error!";	
fi

exit 0;
