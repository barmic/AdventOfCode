#!/usr/bin/env perl

use strict;
use warnings;
use 5.30.0;
use experimental qw(switch);

sub inc {
  my $cycles=shift;
  my $X=shift;
  if ($cycles % 40 == 20) {
    return $cycles * $X;
  } else {
    return 0;
  }
}

my $cycles = 1;
my $X = 1;
my $sum=0;

foreach my $line (<STDIN>) {
  chomp $line;
  given($line) {
    when(/noop/) {
      $cycles+=1;
    }
    when(/addx ([\-0-9]+)/) {
      $sum+=inc($cycles + 1, $X);
      $cycles+=2;
      $X+=$1;
    }
  }
  $sum+=inc($cycles, $X);
}

say $sum;

