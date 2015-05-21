#!/bin/sh
echo "========== running Logistic benchmark =========="
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`

#mode="inputsize"
#mode="iter"
#mode="partition"
#mode="mem"
#mode="mem_par"
mode="feature"
#mode="sercmp"

if [ $mode = "partition" ]; then
	n="0.5"
	sed -i "/memoryFraction=0/ c memoryFraction=$n" bin/config.sh; 
	n="70000000"
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
		sed -i "/memoryFraction=0/ c memoryFraction=$n" bin/config.sh; 
		bin/run.sh; 
	done
elif [ $mode = "feature" ]; then	
	#vals="0.15 0.1 0.05"
	# 70M =data of4G
	n="0.5"
	sed -i "/memoryFraction=0/ c memoryFraction=$n" bin/config.sh; 
	n="25000000"	
	sed -i "/NUM_OF_EXAMPLES=/ c NUM_OF_EXAMPLES=$n" bin/config.sh;
	n="720"
	sed -i "/NUM_OF_PARTITIONS=/ c NUM_OF_PARTITIONS=$n" bin/config.sh; 
	vals="4 8 12 16 20"
	for n in $vals; do 	
		echo "number of features $n"
		sed -i "/NUM_OF_FEATURES=/ c NUM_OF_FEATURES=$n" bin/config.sh;
		sed -i "/memoryFraction=0/ c memoryFraction=0.1" bin/config.sh; 
		bin/gen_data.sh;
		sed -i "/memoryFraction=0/ c memoryFraction=0.5" bin/config.sh; 
		bin/run.sh; 		
	done		
elif [ $mode = "mem_par" ]; then
	n="210000000"
	sed -i "/NUM_OF_EXAMPLES=/ c NUM_OF_EXAMPLES=$n" bin/config.sh;
	for((p=220;p<=2100;p+=80)); do
		sed -i "/NUM_OF_PARTITIONS=/ c NUM_OF_PARTITIONS=$p" bin/config.sh; 		
		va="0.5 0.4 0.2";
		for n in $va; do 	
			echo "memoryfraction $n"
			sed -i "/memoryFraction=0/ c memoryFraction=$n" bin/config.sh; 
			bin/gen_data.sh; 
			bin/run.sh; 
		done	
	done	
elif [ $mode = "inputsize_old" ]; then	
	n="4";
	sed -i "/NUM_OF_FEATURES=/ c NUM_OF_FEATURES=$n" bin/config.sh;
	n="0.5"
	sed -i "/memoryFraction=0/ c memoryFraction=$n" bin/config.sh; 	
	n="720"
	sed -i "/NUM_OF_PARTITIONS=/ c NUM_OF_PARTITIONS=$n" bin/config.sh; 
	vals="10000000 50000000 100000000 200000000 300000000 400000000"
	for n in $vals; do 	
		echo "number of example $n"
		sed -i "/NUM_OF_EXAMPLES=/ c NUM_OF_EXAMPLES=$n" bin/config.sh;
		bin/gen_data.sh;		
		bin/run.sh; 		
	done
elif [ $mode = "iter" ]; then	
	vals="4 6 8 10 12"
	for n in $vals; do 	
		echo "number of iteration $n"
		sed -i "/MAX_ITERATION=/ c MAX_ITERATION=$n" bin/config.sh; 
		bin/run.sh; 
	done	
elif [ $mode = "inputsize" ]; then	
	file=$DIR/bin/config.sh

	for i in 1 5 10 15 20 25 30 35 40; do
	#for i in 4 8 12 16 20 24; do
		base=12.5 #7.5 million data points =1G size of data
					
												
		total=`echo "$base*$i*1000000"|bc`; 	total=`printf "%.0f\n" $total`;
		sed  -i '/NUM_OF_EXAMPLES=/ c NUM_OF_EXAMPLES='$total $file
		 
		totmem=5.2;
		memf=`awk 'BEGIN{printf("%.2f\n",('$i'+1)/'$totmem'/10)}' `
			
		 
		sed -i '/memoryFraction=0/ c memoryFraction='$memf $file 
		numpar=720;   if [ $i -le 20 ];then  numpar=360; fi
		sed -i '/NUM_OF_PARTITIONS=/ c NUM_OF_PARTITIONS='$numpar $file
			
		echo "echo $i G data: NUM_OF_EXAMPLE=$total partition $numpar; memfraction $memf"			
		$DIR/bin/gen_data.sh;sleep 60;
		$DIR/bin/run.sh;	sleep 60
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
	sed -i '/memoryFraction=0/ c memoryFraction=0.346' $file 		
	sed -i '/spark_ser=/ c spark_ser=JavaSerializer' $file			
	#$DIR/bin/run.sh;echo "==java serializer finished==";sleep 60;
	
	sed -i '/memoryFraction=0/ c memoryFraction=0.79' $file 		
	sed -i '/spark_ser=/ c spark_ser=KryoSerializer' $file										
    #$DIR/bin/run.sh; echo "==kryo serializer finished=="; sleep 60;
	 
	#totmem=5.2; 	memf=`awk 'BEGIN{printf("%.2f\n",('$i'+1)/'$totmem'/10)}' `			 
	#sed -i '/memoryFraction=0/ c memoryFraction='$memf $file 		
	#echo "echo $i G data: NUM_OF_EXAMPLE=$total partition $numpar; memfraction $memf"
	
	
	
	sed  -i '/rdd_compression=/ c rdd_compression=true' $file	
	sed -i '/spark_ser=/ c spark_ser=JavaSerializer' $file			
	sed -i '/rddcodec=/ c rddcodec=snappy' $file			
	$DIR/bin/run.sh;echo "==snappy java serializer finished==";sleep 60;
	sed -i '/rddcodec=/ c rddcodec=lzf' $file				
	$DIR/bin/run.sh;echo "==lzf java serializer finished==";sleep 60;
	sed -i '/rddcodec=/ c rddcodec=lz4' $file				
	$DIR/bin/run.sh;echo "==lz4 java serializer finished==";sleep 60;
	
else 
	echo "error!";	
fi

exit 0;