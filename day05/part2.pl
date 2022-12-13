#!/usr/bin/env perl

use strict;
use warnings;
use 5.32.0;

use experimental qw(switch);

my @stacks=();

sub readStacks {
  my $line = shift;
  foreach((0..int(length($line)/4)-1)) {
    $stacks[$_]="" unless exists($stacks[$_]);
    my $elem=substr $line, (1+4*$_), 1;
    $stacks[$_]=$elem.$stacks[$_] if $elem ne ' ';
  }
}

sub move {
  my $line = shift;
  if ($line =~ /move (\d+) from (\d+) to (\d+)/) {
    my $move=substr $stacks[$2-1], length($stacks[$2-1])-$1;
    $stacks[$3-1].=$move;
    $stacks[$2-1]=substr $stacks[$2-1], 0, length($stacks[$2-1])-$1;
  }
}

foreach my $line (<STDIN>) {
  given($line) {
    when(/\[/) {
      readStacks($_);
    }
    when(/move/) {
      move($_);
    }
  }
}

say map chop, @stacks;

