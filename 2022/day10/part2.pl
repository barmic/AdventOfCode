#!/usr/bin/env perl

use strict;
use warnings;
use 5.30.0;
use experimental qw(switch);

sub pixel {
  my $offset=shift;
  my $X=shift;
  my $s=grep {$offset % 40 == $_ } map {$X + $_} (-1..1);
  my $pixel=$s > 0 ? '#' : '_';
  return $pixel;
}

my $X = 1;
my $pixels='';
foreach my $line (<STDIN>) {
  chomp $line;
  $pixels=$pixels.(pixel(length($pixels), $X));
  if($line =~ /addx ([\-0-9]+)/) {
    $pixels=$pixels.(pixel(length($pixels), $X));
    $X+=$1;
  }
}

foreach ((0..5)) {
  say substr $pixels, $_ * 40, 40;
}

