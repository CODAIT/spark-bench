#!/bin/bash
set -eu

realpath () {
(
  TARGET_FILE="$1"

  cd "$(dirname "$TARGET_FILE")"
  TARGET_FILE="$(basename "$TARGET_FILE")"

  COUNT=0
  while [ -L "$TARGET_FILE" -a $COUNT -lt 100 ]
  do
      TARGET_FILE="$(readlink "$TARGET_FILE")"
      cd $(dirname "$TARGET_FILE")
      TARGET_FILE="$(basename $TARGET_FILE)"
      COUNT=$(($COUNT + 1))
  done

  echo "$(pwd -P)/"$TARGET_FILE""
)
}

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
