#!/bin/bash
set -eu

bin=$(dirname $(realpath $0))
basedir=$(dirname "$bin")
if [[ -d "$basedir/lib" ]]
    then jars="$basedir/lib"
elif [[ -d "$basedir/target/assembly" ]]
    then jars="$basedir/target/assembly"
else
    echo "Could not find spark-bench JARs." >&2
    exit 1
fi

[[ -f "$bin/spark-bench-env.sh" ]] && source "$bin/spark-bench-env.sh"

mainclass="com.ibm.sparktc.sparkbench.sparklaunch.SparkLaunch"
launchjar=$(ls "$jars"/spark-bench-launch-[0-9]*.jar)
sparkbenchjar=$(ls "$jars"/spark-bench-[0-9]*.jar)

java -cp "$launchjar" "$mainclass" "$@"
