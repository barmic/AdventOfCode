use std::env;
use std::io::{self, BufRead};

fn prev(suite: Vec<i64>) -> i64 {
    let s: i64 = suite.iter().sum();
    if s == 0 {
        return 0;
    }
    let mut deeper: Vec<i64> = Vec::new();
    for i in 1..suite.len() {
        deeper.push(suite.get(i).unwrap() - suite.get(i - 1).unwrap())
    }
    return suite.first().unwrap() - prev(deeper);
}

fn next(suite: Vec<i64>) -> i64 {
    let s: i64 = suite.iter().sum();
    if s == 0 {
        return 0;
    }
    let mut deeper: Vec<i64> = Vec::new();
    for i in 1..suite.len() {
        deeper.push(suite.get(i).unwrap() - suite.get(i - 1).unwrap())
    }
    return next(deeper) + suite.last().unwrap();
}

fn part1() -> i64 {
    let mut result: i64 = 0;
    for (_, line) in io::stdin().lock().lines().enumerate() {
        let l = line.unwrap();
        let v: Vec<i64> = l.split(" ").map(|s| s.parse::<i64>().unwrap()).collect();
        result += next(v);
    }
    
    return result;
}

fn part2() -> i64 {
    let mut result: i64 = 0;
    for (_, line) in io::stdin().lock().lines().enumerate() {
        let l = line.unwrap();
        let v: Vec<i64> = l.split(" ").map(|s| s.parse::<i64>().unwrap()).collect();
        result += prev(v);
    }
    
    return result;
}

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() > 1 && args[1] == "part2" {
        println!("Part 2: {}", part2());
    } else {
        println!("Part 1: {}", part1());
    }
}
