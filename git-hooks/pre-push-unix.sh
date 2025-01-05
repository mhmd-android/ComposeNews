#!/bin/bash

echo "Running static analysis."

./gradlew lintKotlin
./gradlew detekt
echo "Running static analysis completed"