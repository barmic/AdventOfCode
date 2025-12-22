import readline from 'readline';

type code = number | code[];
type Result = 'ok' | 'ko' | 'unknown' | 'error';

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
  terminal: false
});

const firstMarker = [[2]];
const secondMarker = [[6]];
let packets: code[] = [firstMarker, secondMarker];

rl.on('line', (line) => {
    const packet = eval(line);
    if (packet) {
        packets.push(packet);
    }
});

const sorting: Record<Result, number> = {
    'ok': -1,
    'ko': 1,
    'unknown': 0,
    'error': -1000
}

rl.once('close', () => {
    packets.sort((a, b) => sorting[evaluate(a, b)]);
    const first = packets.findIndex((code) => code === firstMarker)+1;
    const second = packets.findIndex((code) => code === secondMarker)+1;
    console.log(first, second, first * second);
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