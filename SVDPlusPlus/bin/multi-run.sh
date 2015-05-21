#!/bin/sh

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
echo "========== running ${APP} benchmark =========="
mode="sercmp"
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
elif [ $mode = "sercmp" ]; then	
	#the combinations[5+1]: 
	#assuming the case of de-serialization, has been tested out
	#make sure the serialization is right; StorageLevel in configuration file
	
	#what to change: JavaSerializer, KryoSerializer; turn off compression
	#for each combination RDD cache size: need to get the right rdd size for the best performance numbers
	#best serialization + compression: snappy, lz4, lzf
	
	file=$DIR/bin/config.sh

	sed  -i '/rdd_compression=/ c rdd_compression=false' $file	
	sed -i '/memoryFraction=0/ c memoryFraction=0.5' $file 		
	sed -i '/spark_ser=/ c spark_ser=JavaSerializer' $file			
	#$DIR/bin/run.sh;echo "==java serializer finished==";sleep 60;
	
	sed -i '/memoryFraction=0/ c memoryFraction=0.5' $file 		
	sed -i '/spark_ser=/ c spark_ser=KryoSerializer' $file										
    #$DIR/bin/run.sh; echo "==kryo serializer finished=="; sleep 60;
	 
	#totmem=5.2; 	memf=`awk 'BEGIN{printf("%.2f\n",('$i'+1)/'$totmem'/10)}' `			 
	#sed -i '/memoryFraction=0/ c memoryFraction='$memf $file 		
	#echo "echo $i G data: NUM_OF_EXAMPLE=$total partition $numpar; memfraction $memf"
	
	if [ 1 -eq 1 ];then
		sed -i '/memoryFraction=0/ c memoryFraction=0.5' $file 		
		sed  -i '/rdd_compression=/ c rdd_compression=true' $file	
		sed -i '/spark_ser=/ c spark_ser=KryoSerializer' $file			
		sed -i '/rddcodec=/ c rddcodec=snappy' $file			
		$DIR/bin/run.sh;echo "==snappy java serializer finished==";sleep 60;
		
		sed -i '/rddcodec=/ c rddcodec=lzf' $file				
		$DIR/bin/run.sh;echo "==lzf java serializer finished==";sleep 60;
		
		sed -i '/rddcodec=/ c rddcodec=lz4' $file				
		$DIR/bin/run.sh;echo "==lz4 java serializer finished==";sleep 60;	
	fi
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
	#vals="3000000 2000000 1000000 500000 100000"
#	vals="  100000 500000  1000000 2000000 3000000"
	vals="  1000 5000  10000 20000 30000"
	for n in $vals; do 	
		echo "number of verticies $n"
		sed -i "/numV=/ c numV=$n" bin/config.sh;
		bin/gen_data.sh; 
		bin/run.sh;
	done
else 
	echo "error!";	
fi

exit 0;
