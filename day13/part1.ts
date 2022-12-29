import readline from 'readline';

type code = number | code[];
type Result = 'ok' | 'ko' | 'unknown' | 'error';

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
  terminal: false
});

let left: code | undefined = undefined;
let indice = 0;
let goods: number[] = [0];

rl.on('line', (line) => {
    if (!left) {
        left = eval(line);
    } else {
        const right = eval(line);
        indice += 1;
        const result = evaluate(left, right);
        if (result == 'ok') {
            goods.push(indice);
        }
        left = undefined;
    }
});

rl.once('close', () => {
     console.log('results', goods.reduce((a, b) => a + b))
 });

 function evaluate(left: code, right: code): Result {
    if (Array.isArray(left) && Array.isArray(right)) {
        for (let i = 0; i < Math.min(left.length, right.length); i++) {
            const r = evaluate(left?.[i], right[i]);
            if (r !== 'unknown') {
                return r;
            }
        }
        return evaluate(left.length, right.length);
    } else if (typeof left === 'number' && typeof right === 'number') {
        if (left === right) {
            return 'unknown';
        } else {
            const r = left < right ? 'ok' : 'ko';
            return r;
        }
    } else if (typeof left == 'number') {
        return evaluate([left], right);
    } else if (typeof right == 'number') {
        return evaluate(left, [right]);
    }
    return 'error';
 }