#!/usr/bin/env perl

use strict;
use warnings;
use 5.30.0;
use experimental qw(switch);

sub countTree {
  my $line=shift;
  my $prev=-1;
  my @result=();
  foreach my $elem (0..length($line)-1) {
    my $current=substr $line, $elem,1;
    if ($prev < $current) {
      push @result, $elem;
      $prev=$current;
    }
  }
  $prev=-1;
  foreach my $elem (0..length($line)-1) {
    my $relem=length($line)-1-$elem;
    my $current=substr $line, $relem, 1;
    if ($prev < $current) {
      push @result, $relem;
      $prev=$current;
    }
  }
  return join ':', @result;
}

my @columns=();
my @trees=();

my $l=0;
foreach my $line (<STDIN>) {
  chomp $line;

  foreach $tree (map { $l.'_'.$_} split /:/, countTree($line)) {
    push @trees, $tree;
  }

  $l++;
}

