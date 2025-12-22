#!/bin/sh

perl -MArray::Utils=intersect -lnE '$s=length($_)/2;@a=split //, substr($_, 0, $s);@b=split //, substr($_, $s);$v=[intersect(@a, @b)]->[0];$o=ord($v);$sum+=$o>=97?$o-96:$o-38}END{say $sum' $1
