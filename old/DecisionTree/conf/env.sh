# for gen_data.sh;  200M data size = 1 million points
NUM_OF_EXAMPLES=500 #00000
NUM_OF_FEATURES=6
#NUM_OF_PARTITIONS=120 #0

# for run.sh
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

SPARK_STORAGE_MEMORYFRACTION=0.79
