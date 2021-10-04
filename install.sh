#!/bin/bash

set -e

DIR=$(dirname $0)

pushd "$DIR" > /dev/null
./gradlew clean installDist setupScripts
popd

"$DIR"/util/build/setup/install.sh
"$DIR"/util/build/setup/post-install.sh
