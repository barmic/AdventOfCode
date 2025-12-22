#!/bin/awk -f

BEGIN{
  m=0
}
/./{
  c+=$1
}
/^$/{
  if(c > m){
    m=c
  }
  c=0
}
END{
  if(c>m){
    m=c
  }
  print m
}
