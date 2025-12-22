#!/bin/env perl

use strict;
use warnings;
use 5.32.0;
use Array::Utils qw(:all);

my $line = <STDIN>;

my $i=0;

my @maybeMarker=();
do {
  my @m=split //, substr $line, $i, 4;
  @maybeMarker=unique(@m);
  $i+=1;
} while($#maybeMarker + 1 < 4);

say $i+3;
