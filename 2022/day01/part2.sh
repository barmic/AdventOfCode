#!/bin/sh

awk '/./{l=l" "$1}/^$/{print l;l=""}END{print l}' $1 | awk '{for (i=1;i<=NF;++i){a+=$i}print a;a=0}' | sort -n | tail -n3 | awk '{a+=$1}END{print a}'
