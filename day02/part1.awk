#!/bin/awk -f

/X/{c+=1}
/Y/{c+=2}
/Z/{c+=3}
/A Y/||/B Z/||/C X/{c+=6}
/A X/||/B Y/||/C Z/{c+=3}
END{print c}
