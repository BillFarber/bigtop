#!/bin/bash
################################################################
# This script configures BigTopContent database for BigTop Task 4
#
# Usage:
#
# initialize-ml.sh -u <admin-username> -p <admin-password> -h <host-name>
#     -u  The admin username to create
#     -p  The password for the created admin username
#     -h  The hostname of the MarkLogic server
################################################################
USER=""
PASS=""
ML_HOST=""

################################################################
# Default curl authorization mode. MarkLogic uses "digest" as
# the default.
AUTH_MODE="basic"

# when MarkLogic restarts, how many times to check before deciding
# that the restart failed.
N_RETRY=5
# Sleep for this many seconds before trying to see if MarkLogic
# has restarted.
RETRY_INTERVAL=10

#######################################################
# Parse the command line.
########################################################
# Check that at least 5 arguments were given for the
# admin, password, and host. If not, exit with error.
if [ $# -ge 5 ]; then 
  OPTIND=1
  while getopts ":h:p:u:" opt; do
    case "$opt" in
      h) ML_HOST=$OPTARG ;;
      p) PASS=$OPTARG ;;
      u) USER=$OPTARG ;;
      \?) echo "Unrecognized option: -$OPTARG" >&2; exit 1 ;;
    esac
  done
  shift $((OPTIND-1))
else
  echo "ERROR: Desired admin username and password and host are required." >&2
  echo "USAGE: initialize-ml.sh -u <admin-username> -p <admin-password> -h <host-name>" >&2
  exit 1
fi

#######################################################
# Build base CURL commands
########################################################
# Curl command for all requests. Suppress progress meter (-s),
# but still show errors (-S)
CURL="curl -s -S"
# Curl command when authentication is required, after security
# is initialized.
AUTH_CURL="${CURL} --${AUTH_MODE} --user ${USER}:${PASS}"


#######################################################
# Add CAC Search Options
########################################################
echo "Add CAC Search Options"
ADD_CAC_SEARCH_OPTIONS=`$AUTH_CURL -k -X PUT -H "Content-Type:application/xml" \
	-d @cacSearchOptions.xml https://$ML_HOST:8011/v1/config/query/cacOptions`
echo "Add CAC Search Options: $ADD_CAC_SEARCH_OPTIONS"


#######################################################
# Deploy CAC Search Extension
########################################################
echo "Deploy CAC Search Extension"
DEPLOY_CAC_SEARCH_EXTENSION=`$AUTH_CURL -k -X PUT -i -H "Content-Type:application/vnd.marklogic-javascript" \
	--data-binary @extensions/cac-search.sjs https://$ML_HOST:8011/v1/config/resources/cac-search`
echo "Deploy CAC Search Extension: $DEPLOY_CAC_SEARCH_EXTENSION"
