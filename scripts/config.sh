#!/bin/sh

set -eux

cd $(dirname $0)/../

mkdir -p config

export BUILD_DATE=$(date "+%Y-%m-%d-%H-%M")

envsubst < ./scripts/env.gradle.template > ./config/env.gradle
