#!/bin/sh
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"
echo "========== running ${APP} benchmark =========="

#mode="inputsize"
#mode="partition"
#mode="mem"
#mode="mem_par"
mode="sercmp"

if [ $mode = "partition" ]; then
	n="0.5"
	sed -i "/memoryFraction=/ c memoryFraction=$n" bin/config.sh; 
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
elif [ $mode = "sercmp" ]; then	
	#the combinations[5+1]: 
	#assuming the case of de-serialization, has been tested out
	#make sure the serialization is right; StorageLevel in configuration file
	
	#what to change: JavaSerializer, KryoSerializer; turn off compression
	#for each combination RDD cache size: need to get the right rdd size for the best performance numbers
	#best serialization + compression: snappy, lz4, lzf
	
	file=$DIR/bin/config.sh


	 
	#totmem=5.2; 	memf=`awk 'BEGIN{printf("%.2f\n",('$i'+1)/'$totmem'/10)}' `			 
	#sed -i '/memoryFraction=0/ c memoryFraction='$memf $file 		
	#echo "echo $i G data: NUM_OF_EXAMPLE=$total partition $numpar; memfraction $memf"
	sed  -i '/rdd_compression=/ c rdd_compression=false' $file	
	sed -i '/memoryFraction=0/ c memoryFraction=0.02' $file 		
	sed -i '/spark_ser=/ c spark_ser=JavaSerializer' $file			
	$DIR/bin/run.sh $APP;echo "==java serializer finished==";sleep 60;
	
	sed -i '/memoryFraction=0/ c memoryFraction=0.02' $file 		
	sed -i '/spark_ser=/ c spark_ser=KryoSerializer' $file										
    $DIR/bin/run.sh $APP; echo "==kryo serializer finished=="; sleep 60;
	
	if [ 1 -eq 1 ];then
		sed -i '/memoryFraction=0/ c memoryFraction=0.015' $file 		
		sed -i '/rdd_compression=/ c rdd_compression=true' $file	
		sed -i '/spark_ser=/ c spark_ser=KryoSerializer' $file			
		
		
		sed -i '/rddcodec=/ c rddcodec=lzf' $file				
		$DIR/bin/run.sh $APP;echo "==lzf  serializer finished==";sleep 60;
		
		sed -i '/rddcodec=/ c rddcodec=lz4' $file				
		$DIR/bin/run.sh $APP;echo "==lz4  serializer finished==";sleep 60;	
		
		sed -i '/rddcodec=/ c rddcodec=snappy' $file			
		$DIR/bin/run.sh $APP;echo "==snappy  serializer finished==";sleep 60;
	fi	
	
	
	
elif [ $mode = "mem" ]; then
	bin/gen_data.sh; 
	vals="0.15 0.1 0.05"
	for n in $vals; do 	
		echo "memoryfraction $n"
		sed -i "/memoryFraction=/ c memoryFraction=$n" bin/config.sh; 
		bin/run.sh; 
	done
elif [ $mode = "mem_par" ]; then
	bin/gen_data.sh; 
	va="0.15 0.1 0.05"
	for n in $va; do 	
		echo "memoryfraction $n"
		sed -i "/memoryFraction=/ c memoryFraction=$n" bin/config.sh; 
		bin/run.sh; 
	done	
elif [ $mode = "inputsize" ]; then	
	#vals="0.15 0.1 0.05"
	# 70M =data of4G
	vals="70000000"
	for n in $vals; do 	
		echo "number of example $n"
		sed -i "/NUM_OF_EXAMPLES=/ c NUM_OF_EXAMPLES=$n" bin/config.sh;
		bin/gen_data.sh; 				
	done
else 
	echo "error!";	
fi

exit 0;