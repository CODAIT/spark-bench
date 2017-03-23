#!/bin/sh
echo "========== running LinearRegression benchmark =========="
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`

mode="cores"
#mode="inputsize"
#mode="partition"
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
	n="4000000"	
	sed -i "/NUM_OF_EXAMPLES=/ c NUM_OF_EXAMPLES=$n" bin/config.sh;
	n="720"
	sed -i "/NUM_OF_PARTITIONS=/ c NUM_OF_PARTITIONS=$n" bin/config.sh; 
	vals="4 8 12 16 20"
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
	n="4";
	sed -i "/NUM_OF_FEATURES=/ c NUM_OF_FEATURES=$n" bin/config.sh;
	n="0.6"
	sed -i "/memoryFraction=/ c memoryFraction=$n" bin/config.sh; 	
	n="720"
	sed -i "/NUM_OF_PARTITIONS=/ c NUM_OF_PARTITIONS=$n" bin/config.sh; 
	vals="10000000 50000000 100000000 200000000 300000000"
	for n in $vals; do 	
		echo "number of example $n"
		sed -i "/NUM_OF_EXAMPLES=/ c NUM_OF_EXAMPLES=$n" bin/config.sh;
		bin/gen_data.sh;		
		bin/run.sh; 		
	done
elif [ $mode = "cores" ]; then		
	n="4";
	sed -i "/NUM_OF_FEATURES=/ c NUM_OF_FEATURES=$n" bin/config.sh;
	n="0.5"
	sed -i "/memoryFraction=/ c memoryFraction=$n" bin/config.sh; 	
	n="720"
	sed -i "/NUM_OF_PARTITIONS=/ c NUM_OF_PARTITIONS=$n" bin/config.sh; 
	vals="1 2 3 4 5 6 7 8"
	for n in $vals; do 			
		echo "number of cores $n"
		sed -i "/ecore=/ c ecore=$n" bin/config.sh;			
		bin/run.sh; 		
	done
else 
	echo "error!";	
fi

exit 0;
