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

echo "Create default REST server"
REST_CREATE_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d '{"rest-api": { "name": "BigTopServer", "port": "8011", "database": "BigTopContent", "modules-database": "BigTopModules" } }' \
	http://$ML_HOST:8002/v1/rest-apis`
echo "Create REST server response: $REST_CREATE_REPLY"

echo "Request restart"
REST_RESTART_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d '{"operation": "restart-local-cluster"}' http://$ML_HOST:8002/manage/v2`
echo "Restart response: $REST_RESTART_REPLY"

TIMESTAMP=`$AUTH_CURL "http://$ML_HOST:8001/admin/v1/timestamp"`
echo "After ML start TIMESTAMP=$TIMESTAMP"

echo "Create LDAP external security"
CREATE_LDAP_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d @/vagrant/resources/marklogic/ExternalConfig.json http://$ML_HOST:8002/manage/v2/external-security`
echo "Create LDAP external security response: $CREATE_LDAP_REPLY"

echo "Update the REST server with the new LDAP external security"
UPDATE_SERVER_REPLY=`$AUTH_CURL -X PUT -H "Content-type: application/json" \
	-d '{"authentication":"basic", "internal-security":false,"default-user":"nobody","external-security":"BigTop-LDAP-security" }' \
	http://$ML_HOST:8002/manage/v2/servers/BigTopServer/properties?group-id=Default`
echo "Update the REST server with the new LDAP external security response: $UPDATE_SERVER_REPLY"

TIMESTAMP=`$AUTH_CURL "http://$ML_HOST:8001/admin/v1/timestamp"`
echo "After ML start TIMESTAMP=$TIMESTAMP"

echo "Request restart"
REST_RESTART_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d '{"operation": "restart-local-cluster"}' http://$ML_HOST:8002/manage/v2`
echo "Restart response: $REST_RESTART_REPLY"

restart_check $ML_HOST $TIMESTAMP 95

echo "Create BigTopAdminRole to give users the rest-reader execute privilege and access"
CREATE_ROLE_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d '{"role-name":"BigTopAdminRole", "external-name":["cn=bigtopadmingroup,cn=groups,cn=accounts,dc=bigtop,dc=local"], "role":["rest-writer"]}' \
	http://$ML_HOST:8002/manage/v2/roles`
echo "Create BigTopAdminRole response: $CREATE_ROLE_REPLY"

echo "Create BigTopUsersRole to give users the rest-reader execute privilege and access"
CREATE_ROLE_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d '{"role-name":"BigTopUsersRole", "external-name":["cn=bigtopusers,cn=groups,cn=accounts,dc=bigtop,dc=local"], "privilege":[{"privilege-name":"rest-reader", "action":"http://marklogic.com/xdmp/privileges/rest-reader","kind":"execute"}]}' \
	http://$ML_HOST:8002/manage/v2/roles`
echo "Create BigTopUsersRole response: $CREATE_ROLE_REPLY"

echo "Create BigTopReaderRole_red to give users the rest-reader execute privilege and access"
CREATE_ROLE_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d '{"role-name":"BigTopReaderRole_red", "external-name":["cn=bigtopreaderredgroup,cn=groups,cn=accounts,dc=bigtop,dc=local"]}' \
	http://$ML_HOST:8002/manage/v2/roles`
echo "Create BigTopReaderRole_red response: $CREATE_ROLE_REPLY"

echo "Create BigTopReaderRole_blue to give users the rest-reader execute privilege and access"
CREATE_ROLE_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d '{"role-name":"BigTopReaderRole_blue", "external-name":["cn=bigtopreaderbluegroup,cn=groups,cn=accounts,dc=bigtop,dc=local"]}' \
	http://$ML_HOST:8002/manage/v2/roles`
echo "Create BigTopReaderRole_blue response: $CREATE_ROLE_REPLY"

restart_check $ML_HOST $TIMESTAMP 95
