#!/bin/sh

perl -MArray::Utils=intersect -lnE '$l=$.%3;@c=split //;@a=@c if $l==1;@a=intersect(@a,@c);if($l==0){$o=ord(${a[0]});$sum+=$o>=97?$o-96:$o-38}}END{say $sum' $1
