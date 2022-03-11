#!/bin/sh

set -eux

cd $(dirname $0)/../

envsubst < ./env.gradle.template > ./env.gradle
