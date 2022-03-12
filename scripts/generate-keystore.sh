#!/bin/sh

set -eux

cd $(dirname $0)/../

keystore=${KEYSTORE_FILE}
validity=${VALIDITY}
storepass=${KEYSTORE_PASSWORD}
alias=${KEY_ALIAS}
dname=${DISTINGUISHED_NAME}
outputDir=${KEYSTORE_OUTPUT_DIR}

keytool -J-Dkeystore.pkcs12.legacy -genkey -v -keystore ${outputDir}/${keystore} -keyalg RSA -validity ${validity} -storepass ${storepass} -alias ${alias} -dname "${dname}"

keytool -list -v -keystore ${outputDir}/${keystore} -storepass ${storepass}