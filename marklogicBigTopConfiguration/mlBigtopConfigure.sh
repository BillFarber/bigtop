#!/bin/bash
################################################################
# This script configures MarkLogic for the BigTop LDAP demo
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
AUTH_MODE="digest"

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
#echo "Create default REST server response: $REST_CREATE_REPLY"

echo "Request restart"
REST_RESTART_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d '{"operation": "restart-local-cluster"}' http://$ML_HOST:8002/manage/v2`
#echo "Restart response: $REST_RESTART_REPLY"

TIMESTAMP=`$AUTH_CURL "http://$ML_HOST:8001/admin/v1/timestamp"`
echo "After ML start TIMESTAMP=$TIMESTAMP"

echo "Create LDAP external security"
CREATE_LDAP_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d @ExternalConfig.json http://$ML_HOST:8002/manage/v2/external-security`
#echo "Create LDAP external security response: $CREATE_LDAP_REPLY"

echo "Create BigTopCertTemplate"
CREATE_TEMPLATE_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
  -d @BigTopCertTemplate.json http://$ML_HOST:8002/manage/v2/certificate-templates`
#echo "Create BigTopCertTemplate response: $CREATE_TEMPLATE_REPLY"

echo "Update the REST server with the new LDAP external security"
UPDATE_SERVER_REPLY=`$AUTH_CURL -X PUT -H "Content-type: application/json" \
	-d '{"authentication":"basic", "internal-security":false,"default-user":"nobody","external-security":"BigTop-LDAP-security", "ssl-require-client-certificate": false }' \
	http://$ML_HOST:8002/manage/v2/servers/BigTopServer/properties?group-id=Default`
echo "Update the REST server with the new LDAP external security response: $UPDATE_SERVER_REPLY"

TIMESTAMP=`$AUTH_CURL "http://$ML_HOST:8001/admin/v1/timestamp"`
echo "After ML start TIMESTAMP=$TIMESTAMP"

echo "Request restart"
REST_RESTART_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d '{"operation": "restart-local-cluster"}' http://$ML_HOST:8002/manage/v2`
#echo "Restart response: $REST_RESTART_REPLY"

restart_check $ML_HOST $TIMESTAMP 95

echo "Create BigTopAdminRole to give users the rest-reader execute privilege and access"
CREATE_ROLE_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d '{"role-name":"BigTopAdminRole", "external-name":["cn=bigtopadmingroup,cn=groups,cn=accounts,dc=bigtop,dc=local"], "role":["rest-writer"]}' \
	http://$ML_HOST:8002/manage/v2/roles`
#echo "Create BigTopAdminRole response: $CREATE_ROLE_REPLY"

echo "Create BigTopUsersRole to give users the rest-reader execute privilege and access"
CREATE_ROLE_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d '{"role-name":"BigTopUsersRole", "external-name":["cn=bigtopusers,cn=groups,cn=accounts,dc=bigtop,dc=local"], "privilege":[{"privilege-name":"rest-reader", "action":"http://marklogic.com/xdmp/privileges/rest-reader","kind":"execute"}]}' \
	http://$ML_HOST:8002/manage/v2/roles`
#echo "Create BigTopUsersRole response: $CREATE_ROLE_REPLY"

echo "Create BigTopReaderRole_red to give users the rest-reader execute privilege and access"
CREATE_ROLE_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d '{"role-name":"BigTopReaderRole_red", "external-name":["cn=bigtopreaderredgroup,cn=groups,cn=accounts,dc=bigtop,dc=local"]}' \
	http://$ML_HOST:8002/manage/v2/roles`
#echo "Create BigTopReaderRole_red response: $CREATE_ROLE_REPLY"

echo "Create BigTopReaderRole_blue to give users the rest-reader execute privilege and access"
CREATE_ROLE_REPLY=`$AUTH_CURL -X POST -H "Content-Type:application/json" \
	-d '{"role-name":"BigTopReaderRole_blue", "external-name":["cn=bigtopreaderbluegroup,cn=groups,cn=accounts,dc=bigtop,dc=local"]}' \
	http://$ML_HOST:8002/manage/v2/roles`
#echo "Create BigTopReaderRole_blue response: $CREATE_ROLE_REPLY"

restart_check $ML_HOST $TIMESTAMP 95
