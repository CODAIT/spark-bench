#!/bin/bash

DIR=`dirname "$0"`
DIR=`cd "${DIR}/.."; pwd`



for workload in `cat $DIR/applications.lst`; do
    if [[ $workload == \#* ]]; then
        continue
    fi

    echo -e "${UYellow}${BYellow}Prepare ${Yellow}${UYellow}${workload} ${BYellow}...${Color_Off}"
    
    WORKLOAD=$DIR/workloads/${workload}
    echo -e "${BCyan}Exec script: ${Cyan}${WORKLOAD}/prepare/prepare.sh${Color_Off}"
    "${WORKLOAD}/bin/gen_data.sh"

    if [ $? -ne 0 ]
    then
	echo "ERROR: ${workload} prepare failed!" 
        continue
    fi

    
	echo -e "${UYellow}${BYellow}Run ${Yellow}${UYellow}${workload}/${target}${Color_Off}"
	echo -e "${BCyan}Exec script: ${Cyan}$WORKLOAD/${target}/bin/run.sh${Color_Off}"
	$WORKLOAD/bin/run.sh

	result=$?
	if [ $result -ne 0 ]
	then
	    echo -e "${On_IRed}ERROR: ${workload}/${target} failed to run successfully.${Color_Off}" 
            exit $result
	fi
    
done

echo "Run all done!"