echo "========== gen data =========="
# configure
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"

#. "${DIR}/bin/config.sh"
CLASS="src.main.java.MovieReviewConverter"
JAR="${DIR}/DataGen/target/sparkbench-data-gen-project-1.0.jar"
DATA_DIR="${DATASET_DIR}/AmazonMovieReview"
OPT="${DATA_DIR}/movies.txt ${DATA_DIR}/movies-out.txt "
exec java -cp $JAR $CLASS $OPT
