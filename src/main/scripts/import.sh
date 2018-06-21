#!/usr/bin/env bash

function import {
    local URL="${1?Specify a URL}"
    local FILE="${2?Specify a json filename}"

    # Make a temp dir and delete it on exit
    DIR=$(mktemp -d) && trap "rm -rf $DIR" EXIT

    # We allow the first comment in the file to identify
    # the path of the REST service to execute
    #
    # In short, the concept of a shebang used in REST
    #
    local FIRST_LINE="$(head -n 1 $FILE)"
    local POST="$FILE"
    [[ "$FIRST_LINE" == //api/* ]] && {
        URL="$URL$(echo "$FIRST_LINE" | sed 's,^//,/,')"
        POST="$DIR/$(basename $FILE)"
        POST_SUBST="$DIR/$(basename $FILE).subst"
        tail -n +2 "$FILE" > "$POST"
        perl -p -e 's/\$\{(\w+)\}/(exists $ENV{$1}?$ENV{$1}:"missing variable $1")/eg' < ${POST} > ${POST_SUBST}
    }

#    local ID=$RANDOM
#    BODY="/tmp/$(basename $FILE).$ID.body.txt"
#    RESP="/tmp/$(basename $FILE).$ID.resp.txt"
#    perl -pe 's,^  "(id|name|displayName)": "([^"]+)(".*),  "$1": "$2$ENV{ID}$3,g' $FILE > $BODY

# for debugging purpose
# would be better for the perl above to output in the console the missing env variables instead of writing crap in the POST file itself
# echo $POST_SUBST

response="$(
curl -w "%{size_upload} %{size_download} %{time_total} %{http_code} %{url_effective}" -o "$RESP" -s -X POST $URL  \
     --insecure \
     --header 'accept: application/json' \
     --header 'authorization: Basic YWRtaW46YWRtaW4=' \
     --header 'cache-control: no-cache' \
     --header 'content-type: application/json' \
     --data "@$POST_SUBST"
)"

# Truncate file path for output
FILE="$(echo $FILE |perl -pe 's,.+(.{3})(.{32})$,...$2,g')"

FORMAT=' %-5s %-35s %6s %6s %6sr c%s %s\n'
printf "$FORMAT" POST $FILE $response | perl -pe '
     # HTTP Status Code colors
     s,c(2[0-9][0-9]),\033[38;5;022m$1\033[0m,g;
     s,c(3[0-9][0-9]),\033[38;5;003m$1\033[0m,g;
     s,c(4[0-9][0-9]),\033[38;5;025m$1\033[0m,g;
     s,c(5[0-9][0-9]),\033[38;5;088m$1\033[0m,g;
     # Response Time colors
     s,(0\.[0-9]{3})r,\033[38;5;058m$1\033[0m,g;
     s,(1\.[0-9]{3})r,\033[38;5;094m$1\033[0m,g;
     s,(2\.[0-9]{3})r,\033[38;5;130m$1\033[0m,g;
     s,(3\.[0-9]{3})r,\033[38;5;166m$1\033[0m,g;
     s,(4\.[0-9]{3})r,\033[38;5;202m$1\033[0m,g;
     s,([5-9]\.[0-9]{3})r,\033[38;5;124m$1\033[0m,g;
     s,([0-9][0-9]+\.[0-9]{3})r,\033[38;5;196m$1\033[0m,g;
    '
}

# This script is designed to be sourced.  If it isn't being sourced,
# go ahead and execute the artifact function for the user
[[ "${BASH_SOURCE[0]}" = "${0}" ]] &&
import "$@"