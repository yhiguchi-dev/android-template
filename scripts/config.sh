#!/bin/sh

set -eux

cd $(dirname $0)/../

mkdir -p config

envsubst < ./scripts/env.gradle.template > ./config/env.gradle
