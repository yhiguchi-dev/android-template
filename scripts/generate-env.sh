#!/bin/sh

set -eux

cd $(dirname $0)/../

export BUILD_DATE=$(date "+%Y-%m-%d-%H-%M")

envsubst < ./env.gradle.template > ./env.gradle
