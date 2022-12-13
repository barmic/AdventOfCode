#!/bin/awk -f
/Y/{c+=3}
/Z/{c+=6}
/A Y/||/B X/||/C Z/{c+=1}
/A Z/||/B Y/||/C X/{c+=2}
/A X/||/B Z/||/C Y/{c+=3}
END{print c}
