#!/usr/bin/env raku

grammar Monkey {
  rule TOP {
    <monkey>
      <bag>
      <operation>
  }

  rule monkey { 'Monkey' <monkeyid> ':' }
  token monkeyid { \d }

  rule bag { 'Starting items:' <bagitem> }
  token bagitem { \d+ }

  rule operation { 'Operation:' <formula> }
  token formula { .+ }

  rule action {
    <check>
  }
  rule check { 'Test: divisible by' <pivot> }
  token pivot { \d+ }

  #rule case { 'If' ['true' | 'false'] }
  #token TrueCase  { "    If true: throw to monkey " <id> }
  #token FalseCase { "    If false: throw to monkey " <id> }
}

my $input=slurp;
#for lines() {
#  $input=$input.$_;
#}

say $input;
say Monkey.parse($input);

