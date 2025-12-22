#!/bin/sh

perl -MArray::Utils=intersect -laF'[-,]' -nE '@a=${F[0]}..${F[1]};@b=${F[2]}..${F[3]};$int=intersect(@a,@b);$sum+=1 if $int == $#a+1 || $int == $#b+1}END{say $sum' $1
