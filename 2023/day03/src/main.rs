use std::io::{self, BufRead};
use regex::Regex;
use std::env;
use std::collections::HashSet;

fn area(addr: (i32, i32), size: i32) -> HashSet<(i32, i32)> {
    let mut area = HashSet::new();
    for line in (addr.0 - 1)..(addr.0 + 2) {
        for column in (addr.1 - 1)..(addr.1 + size + 1) {
            area.insert((line, column));
        }
    }
    return area;
}

fn part1() -> i32 {
    let token_pattern = Regex::new(r"([^.\d]|\d+)").unwrap();

    let mut lineno = 0;
    let mut symbols = HashSet::new();
    let mut numbs = Vec::new();
    for line in io::stdin().lock().lines() {
        for cap in token_pattern.captures_iter(&line.unwrap()) {
            let (token, [_]) = cap.extract();
            let m = cap.iter().next().unwrap().unwrap();
            let addr = (lineno, m.start() as i32);
            if "0123456789".contains(token.chars().nth(0).unwrap()) {
                numbs.push((token.parse::<i32>().unwrap(), area(addr, token.len() as i32)));
            } else {
                symbols.insert(addr);
            }
        }
        lineno += 1;
    }

    let mut result = 0;
    for num in numbs {
        if num.1.intersection(&symbols).count() > 0 {
            result += num.0;
        }
    }
    return result;
}

fn part2() -> i32 {
    let token_pattern = Regex::new(r"([*]|\d+)").unwrap();

    let mut lineno = 0;
    let mut gears = HashSet::new();
    let mut numbs = Vec::new();
    for line in io::stdin().lock().lines() {
        for cap in token_pattern.captures_iter(&line.unwrap()) {
            let (token, [_]) = cap.extract();
            let m = cap.iter().next().unwrap().unwrap();
            let addr = (lineno, m.start() as i32);
            if "0123456789".contains(token.chars().nth(0).unwrap()) {
                numbs.push((token.parse::<i32>().unwrap(), area(addr, token.len() as i32)));
            } else {
                gears.insert(addr);
            }
        }
        lineno += 1;
    }

    let mut result = 0;
    for gear in gears {
        let mut gear_ratio = Vec::new();
        for num in &numbs {
            if num.1.contains(&gear) {
                gear_ratio.push(num.0);
            }
        }
        if gear_ratio.len() == 2 {
            result += gear_ratio.iter().fold(1, | acc, v | acc * v);
        }
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
