#!/bin/bash
#==DecisionTree==
# paths
INPUT_HDFS=${DATA_HDFS}/DecisionTree/Input
OUTPUT_HDFS_Classification=${DATA_HDFS}/DecisionTree/Output-Classiciation
OUTPUT_HDFS_Regression=${DATA_HDFS}/DecisionTree/Output-Classiciation



#System.out.println("usage: <input> <output> <numClass>"
                    #+ " <impurity> <maxDepth> <maxBins> <mode:Regression/Classification>");
# for prepare #200M=1million points
NUM_OF_EXAMPLES=5000 #00000
NUM_OF_PARTITIONS=120 #0
memoryFraction=0.01
[ -n "$MEM_FRACTION_GLOBAL"  ] && memoryFraction=${MEM_FRACTION_GLOBAL}
NUM_OF_FEATURES=6

# for running
NUM_OF_CLASS_C=10
impurityC="gini"
maxDepthC=5
maxBinsC=100
modeC="Classification"

#${NUM_OF_CLASS_C} ${impurityC} ${maxDepthC} ${maxBinsC} ${modeC}

NUM_OF_CLASS_R=10
impurityR="variance"
maxDepthR=5
maxBinsR=100
modeR="Regression"

#${NUM_OF_CLASS_R} ${impurityR} ${maxDepthR} ${maxBinsR} ${modeR}


MAX_ITERATION=3
NUM_TRIALS=1

#input benreport
function print_config(){
	local output=$1
	echo "DecisionTreeC-Config memoryFraction ${memoryFraction} \
	NUM_OF_EXAMPLES ${NUM_OF_EXAMPLES} \
	NUM_OF_FEATURES ${NUM_OF_FEATURES} \
	NUM_OF_PARTITIONS ${NUM_OF_PARTITIONS} \
	class ${NUM_OF_CLASS_C} ${NUM_OF_CLASS_R} impurity ${impurityC}  ${impurityR} maxDepth ${maxDepthC} ${maxDepthR} maxbin ${maxBinsC} ${maxBinsR} mode ${modeC} ${modeR}" >> ${output}		
}

#### unused ####
if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi
