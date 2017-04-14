#!/bin/bash
################################################################
# This script initializes MarkLogic and creates the admin account.
#
# Usage:
#
# initialize-ml.sh -u <admin-username> -p <admin-password> -r <secuity-realm>
#     -u  The admin username to create
#     -p  The password for the created admin username
#     -r  The desired security realm string, if any. Default: "public"
################################################################

################################################################
# The following are used to create the first MarkLogic
# Administrator account. The values are passed into this script
# from commandline arguements.
################################################################
USER=""
PASS=""
SEC_REALM="public"

################################################################
# Default curl authorization mode. MarkLogic uses "digest" as
# the default.
AUTH_MODE="digest"

# when MarkLogic restarts, how many times to check before deciding
# that the restart failed.
N_RETRY=5
# Sleep for this many seconds before trying to see if MarkLogic
# has restarted.
RETRY_INTERVAL=10

# Get the hostname of this server.
ML_HOST=$HOSTNAME

#######################################################
# Parse the command line. After MarkLogic initializes,
# an administrator account must be created. The desired
# username, password and realm for the admin account is
# given in commandline arguements.
########################################################

# Check that at least 2 arguments were given for the
# admin and password. If not, exit with error.
if [ $# -ge 2 ]; then 
  OPTIND=1
  while getopts ":r:p:u:" opt; do
    case "$opt" in
      r) SEC_REALM=$OPTARG ;;
      p) PASS=$OPTARG ;;
      u) USER=$OPTARG ;;
      \?) echo "Unrecognized option: -$OPTARG" >&2; exit 1 ;;
    esac
  done
  shift $((OPTIND-1))
else
  echo "ERROR: Desired admin username and password are required." >&2
  echo "USAGE: initialize-ml.sh -u <admin-username> -p <admin-password> [-r <security-realm-string>]" >&2
  exit 1
fi

# Curl command for all requests. Suppress progress meter (-s),
# but still show errors (-S)
CURL="curl -s -S"
# Curl command when authentication is required, after security
# is initialized.
AUTH_CURL="${CURL} --${AUTH_MODE} --user ${USER}:${PASS}"

#######################################################
# restart_check(hostname, baseline_timestamp, caller_lineno)
#
# Use the timestamp service to detect a server restart, given a
# a baseline timestamp. Use N_RETRY and RETRY_INTERVAL to tune
# the test length. Include authentication in the curl command
# so the function works whether or not security is initialized.
#   $1 :  The hostname to test against
#   $2 :  The baseline timestamp
#   $3 :  Invokers LINENO, for improved error reporting
# Returns 0 if restart is detected, exits with an error if not.
#
function restart_check {
  sleep 2
  LAST_START=`$AUTH_CURL "http://$ML_HOST:8001/admin/v1/timestamp"`
  echo "last_start=$LAST_START for host=$1"
  for i in `seq 1 ${N_RETRY}`; do
    if [ "$2" == "$LAST_START" ] || [ "$LAST_START" == "" ]; then
      sleep ${RETRY_INTERVAL}
      LAST_START=`$AUTH_CURL "http://$ML_HOST:8001/admin/v1/timestamp"`
      echo "last_start iteration $i=$LAST_START"
    else
      return 0
    fi
  done
  echo "ERROR: Line $3: Failed to restart $1"
  exit 1
}

TIMESTAMP=`$AUTH_CURL "http://$ML_HOST:8001/admin/v1/timestamp"`
echo "After ML start TIMESTAMP=$TIMESTAMP"

echo "Delete BigTopServer"
REST_DELETE_REPLY=`$AUTH_CURL -X DELETE -H "Content-Type:application/json" \
	http://$ML_HOST:8002/manage/v2/servers/BigTopServer?group-id=Default`
echo "Delete BigTopServer response: $REST_DELETE_REPLY"

echo "Request restart"
REST_RESTART_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d '{"operation": "restart-local-cluster"}' http://$ML_HOST:8002/manage/v2`
echo "Restart response: $REST_RESTART_REPLY"

restart_check $ML_HOST $TIMESTAMP 95

echo "Delete BigTopContent"
DELETE_CONTENT_REPLY=`$AUTH_CURL -X DELETE -H "Content-Type:application/json" \
	http://$ML_HOST:8002/manage/v2/databases/BigTopContent?forest-delete=data`
echo "Delete BigTopServer response: $DELETE_CONTENT_REPLY"

echo "Delete BigTopModules"
DELETE_MODULES_REPLY=`$AUTH_CURL -X DELETE -H "Content-Type:application/json" \
	http://$ML_HOST:8002/manage/v2/databases/BigTopModules?forest-delete=data`
echo "Delete BigTopModules response: $DELETE_MODULES_REPLY"

echo "Delete LDAP external security"
DELETE_LDAP_REPLY=`$AUTH_CURL -X DELETE -H "Content-Type:application/json" \
	http://$ML_HOST:8002/manage/v2/external-security/BigTop-LDAP-security`
echo "Delete LDAP external security response: $DELETE_LDAP_REPLY"

echo "Delete BigTopAdminRole role"
DELETE_ROLE_REPLY=`$AUTH_CURL -X DELETE -H "Content-Type:application/json" \
	http://$ML_HOST:8002/manage/v2/roles/BigTopAdminRole`
echo "Delete BigTopAdminRole role response: $DELETE_ROLE_REPLY"

echo "Delete BigTopUsersRole role"
DELETE_ROLE_REPLY=`$AUTH_CURL -X DELETE -H "Content-Type:application/json" \
	http://$ML_HOST:8002/manage/v2/roles/BigTopUsersRole`
echo "Delete BigTopUsersRole role response: $DELETE_ROLE_REPLY"

echo "Delete BigTopReaderRole_red role"
DELETE_ROLE_REPLY=`$AUTH_CURL -X DELETE -H "Content-Type:application/json" \
	http://$ML_HOST:8002/manage/v2/roles/BigTopReaderRole_red`
echo "Delete BigTopReaderRole_red role response: $DELETE_ROLE_REPLY"

echo "Delete BigTopReaderRole_blue role"
DELETE_ROLE_REPLY=`$AUTH_CURL -X DELETE -H "Content-Type:application/json" \
	http://$ML_HOST:8002/manage/v2/roles/BigTopReaderRole_blue`
echo "Delete BigTopReaderRole_blue role response: $DELETE_ROLE_REPLY"
