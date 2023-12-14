#!/bin/bash

read_xml () {
    local IFS=\>
    read -r -d \< ENTITY CONTENT
    local ret=$?
    TAG_NAME=${ENTITY%% *}
    ATTRIBUTES=${ENTITY#* }
    return $ret
}

unescape_html() {
    echo "$1" | sed 's/\&apos;/\x27/g; s/\&quot;/\x22/g; s/\&lt;/\x3c/g; s/\&gt;/\x3e/g; s/\&amp;/\x26/g'
}

parse_checkstyle () {
    local checkstyle_path=$1

    local errors=()
    local current_file=""

    while read_xml; do
        if [[ $TAG_NAME = "error" ]] ; then
            eval local $ATTRIBUTES
            description=$(unescape_html "$message")
            check_name=$(echo "$source" | awk -F'.' '{print $NF}' | sed 's#/$##')  # Remove the last slash
            error_data='{
                "description": "'"$description"'",
                "check_name": "'"$check_name"'",
                "fingerprint": "'$(echo -n "$message" | md5sum | cut -d' ' -f1)'",
                "severity": "minor",
                "location": {
                    "path": "'"$current_file"'",
                    "positions": {
                        "begin": {
                            "line": '"$line"',
                            "column": '"$column"'
                        }
                    }
                }
            }'
            errors+=("$error_data")
        elif [[ $TAG_NAME = "file" ]] ; then
            eval local $ATTRIBUTES
            current_file=$(echo "$name" | sed 's/\\/\//g')
        fi
    done < "$checkstyle_path"

    echo "["
    local first_error=true
    for error in "${errors[@]}"; do
        if [[ $first_error == false ]]; then
            echo ","
        fi
        echo -n "$error"
        first_error=false
    done
    echo "]"
}


while getopts 'c:' OPTION
do
  case "${OPTION}" in
    c) checkstyle_path=${OPTARG};;
   \?) echo "$0: Error: Invalid option: -${OPTARG}" >&2; exit 1;;
    :) echo "$0: Error: option -${OPTARG} requires an argument" >&2; exit 1;;
  esac
done

shift $((OPTIND - 1))
output_path=${1}

if [ ! -f "$checkstyle_path" ]; then
    echo "Checkstyle file '$checkstyle_path' does not exist"
    exit
fi

output_directory=$(dirname "${output_path}")

if [ ! -w "${output_directory}" ]; then
    echo "Error: The directory ${output_directory} is not writable."
    exit 1
fi

parse_checkstyle "$checkstyle_path" > "$output_path"
