#!/bin/sh

#egrep -won '[0-9]+ (green|red|blue)' input | tr ':' ' ' | awk 'BEGIN{split("",not)}/blue/&&$2>14 || /green/&&$2>13 || /red/&&$2>12{not[$1]=-$1}END{for(i=1;i<=$1;i++){a+=not[i]+i}print a}'
egrep -won '[0-9]+ (green|red|blue)' input | tr ':' ' ' | awk 'BEGIN{split("",not)}/blue/&&$2>14 || /green/&&$2>13 || /red/&&$2>12{not[$1]=-$1}END{for(i=1;i<=$1;i++){if(not[i]+i> 0){print i}}}'
