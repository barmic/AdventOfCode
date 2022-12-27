import readline from 'readline';

type code = [number | [code]];

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
  terminal: false
});

let right: code | undefined = undefined;

rl.on('line', (line) => {
    if (!right) {
        right = eval(line);
    } else {
        const left = eval(line);
        const result = evaluate(left, right);
        console.log('result', result);
        right = undefined;
    }
});

rl.once('close', () => {
     // end of input
 });

 function evaluate(left: code, right: code): boolean {
    return true;
 }