#!/bin/sh

set -eux

cd "$(dirname "$0")"/../

debug=${DEBUG:=false}

if "${debug}"; then
  keystoreFile=debug.keystore
  keystorePassword=android
  keyValidity=10950
  keyAlias=androiddebugkey
  distinguishedName='CN=Android Debug, O=Android, C=US'
  outputDir=./app
else
  keystoreFile=${KEYSTORE_FILE}
  keystorePassword=${KEYSTORE_PASSWORD}
  keyValidity=${KEY_VALIDITY}
  keyAlias=${KEY_ALIAS}
  distinguishedName=${DISTINGUISHED_NAME}
  outputDir=${OUTPUT_DIR}
fi

keytool -J-Dkeystore.pkcs12.legacy -genkey -v -keystore "${outputDir}"/"${keystoreFile}" -keyalg RSA -validity "${keyValidity}" -storepass "${keystorePassword}" -alias "${keyAlias}" -dname "${distinguishedName}"

keytool -list -v -keystore "${outputDir}"/"${keystoreFile}" -storepass "${keystorePassword}"