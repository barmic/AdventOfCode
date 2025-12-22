#!/bin/sh

perl -MArray::Utils=intersect -laF'[-,]' -nE '@a=${F[0]}..${F[1]};@b=${F[2]}..${F[3]};$int=intersect(@a,@b);$sum+=1 if $int > 0}END{say $sum' "$1"
