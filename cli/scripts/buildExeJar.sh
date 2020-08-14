#!/usr/bin/env bash

cat $1 $2 > $3 && chmod +x $3;
cat $1 $2 > $3.jar && chmod +x $3.jar;
