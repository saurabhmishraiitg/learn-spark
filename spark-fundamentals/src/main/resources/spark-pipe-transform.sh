#!/usr/bin/env bash

# Running shell script which takes input from STDIN and write to the STDOUT
while read LINE; do
    echo "my-$LINE"
done
