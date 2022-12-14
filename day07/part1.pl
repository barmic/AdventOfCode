#!/usr/bin/env perl

use strict;
use warnings;
use 5.30.0;
use experimental qw(switch);

my @path=();
my %index=();

sub debug {
  foreach(keys(%index)) {
    say $_. ' => '.$index{$_};
  }
}

foreach my $line (<STDIN>) {
  given($line) {
    when(/cd \//) {
      @path=('root');
    }
    when(/cd (\w+)/) {
      my $current=$path[$#path].'/'.$1;
      push @path, $current;
      $index{$current}=0;
    }
    when(/cd \.\./) {
      pop @path;
    }
    when(/^(\d+) /) {
      foreach(@path) {
        $index{$_}+=$1;
      }
    }
  }
}

#debug
my $sum=0;
foreach(values(%index)) {
  $sum+=$_ if $_ >= 100000; 
}
say $sum;
