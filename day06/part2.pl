#!/bin/env perl

use strict;
use warnings;
use 5.32.0;
use Array::Utils qw(:all);

my $line = <STDIN>;

my $i=0;

my @maybeMarker=();
do {
  my @m=split //, substr $line, $i, 14;
  @maybeMarker=unique(@m);
  $i+=1;
} while($#maybeMarker + 1 < 14);

say $i+13;
