#!/bin/sh

set -eux

cd $(dirname $0)/../

keystore=${KEYSTORE_FILE}
validity=${VALIDITY}
storepass=${KEYSTORE_PASSWORD}
alias=${KEY_ALIAS}
dname=${DISTINGUISHED_NAME}

keytool -J-Dkeystore.pkcs12.legacy -genkey -v -keystore ./${keystore} -keyalg RSA -validity ${validity} -storepass ${storepass} -alias ${alias} -dname "${dname}"

keytool -list -v -keystore ${keystore} -storepass ${storepass}