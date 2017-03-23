#!/bin/sh
echo "========== running SVM benchmark =========="
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`

mode="inputsize"
#mode="partition"
#mode="mem"
#mode="mem_par"
##perform serialization and compression
#mode="sercmp"

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
elif [ $mode = "inputsize_old" ]; then	
	#vals="0.15 0.1 0.05"
	# 70M =data of4G
	#vals="10000000 50000000 100000000 100000000 500000000 "
	vals="200000000 300000000 "
	for n in $vals; do 	
		echo "number of example $n"
		sed -i "/NUM_OF_EXAMPLES=/ c NUM_OF_EXAMPLES=$n" bin/config.sh;
		bin/gen_data.sh; 	
		bin/run.sh;
	done
elif [ $mode = "inputsize" ]; then	
	file=$DIR/bin/config.sh

	for i in 1 5 10 15 20 25 30 35  40; do
#	for i in 4 8 12 16 20 24; do	
		base=7.5;
		total=`echo "$base*$i*1000000"|bc`; 	total=`printf "%.0f\n" $total`;
		sed  -i '/NUM_OF_EXAMPLES=/ c NUM_OF_EXAMPLES='$total $file
		 
		totmem=5.2;
		memf=`awk 'BEGIN{printf("%.2f\n",('$i'+1)/'$totmem'/10)}' `
		sed -i '/memoryFraction=0/ c memoryFraction='$memf $file 
		
		numpar=720;   if [ $i -le 20 ];then  numpar=360; fi
		sed -i '/NUM_OF_PARTITIONS=/ c NUM_OF_PARTITIONS='$numpar $file
			
		echo "echo $i G data: NUM_OF_EXAMPLE=$total partition $numpar; memfraction $memf"
			
		$DIR/bin/gen_data.sh;sleep 60;
		$DIR/bin/run.sh;sleep 60;
			
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
	sed -i '/spark_ser=/ c spark_ser=JavaSerializer' $file			
	$DIR/bin/run.sh;sleep 60;
	
	sed -i '/spark_ser=/ c spark_ser=KryoSerializer' $file										
    $DIR/bin/run.sh;sleep 60;
	 
	totmem=5.2; 	memf=`awk 'BEGIN{printf("%.2f\n",('$i'+1)/'$totmem'/10)}' `			 
	sed -i '/memoryFraction=0/ c memoryFraction='$memf $file 		
	echo "echo $i G data: NUM_OF_EXAMPLE=$total partition $numpar; memfraction $memf"
	
	
	
	sed  -i '/rdd_compression=/ c rdd_compression=true' $file	
	sed -i '/spark_ser=/ c spark_ser=JavaSerializer' $file			
	sed -i '/codec=/ c codec=snappy' $file			
	$DIR/bin/run.sh;sleep 60;
	sed -i '/codec=/ c codec=lzf' $file				
	$DIR/bin/run.sh;sleep 60;
	sed -i '/codec=/ c codec=lz4' $file				
	$DIR/bin/run.sh;sleep 60;
	
else 
	echo "error!";	
fi

exit 0;
