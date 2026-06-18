#!/usr/bin/env sh
set -eu

MAVEN_VERSION="3.9.9"
BASE_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
MAVEN_DIR="$BASE_DIR/.mvn/apache-maven-$MAVEN_VERSION"
MAVEN_TGZ="$BASE_DIR/.mvn/apache-maven-$MAVEN_VERSION-bin.tar.gz"
MAVEN_URL="https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz"

if [ ! -x "$MAVEN_DIR/bin/mvn" ]; then
  mkdir -p "$BASE_DIR/.mvn"
  if [ ! -f "$MAVEN_TGZ" ]; then
    echo "Downloading Maven $MAVEN_VERSION..."
    curl -fsSL "$MAVEN_URL" -o "$MAVEN_TGZ"
  fi
  tar -xzf "$MAVEN_TGZ" -C "$BASE_DIR/.mvn"
fi

exec "$MAVEN_DIR/bin/mvn" "$@"
