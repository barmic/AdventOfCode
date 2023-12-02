use std::io::{self, BufRead};
use regex::Regex;use std::collections::HashMap;
use std::env;

fn part1() -> i32 {
    let count_color = Regex::new(r"([0-9]+) +(red|blue|green)").unwrap();

    let limits = HashMap::from([
        ("red", 12),
        ("green", 13),
        ("blue", 14),
    ]);

    let mut lineno = 1;
    let mut result = 0;
    for line in io::stdin().lock().lines() {
        let mut ok = true;
        for (_, [count_str, color]) in count_color.captures_iter(&line.unwrap()).map(|c| c.extract()) {
            let count: i32 = count_str.parse().unwrap();
            ok &= count <= *limits.get(color).unwrap();
        }
        if ok {
            result += lineno;
        }
        lineno += 1;
    }
    return result;
}

fn part2() -> i32 {
    let count_color = Regex::new(r"([0-9]+) +(red|blue|green)").unwrap();

    let mut result = 0;
    for line in io::stdin().lock().lines() {
        let l = line.unwrap();
        let mut mins = HashMap::from([]);
        for (_, [count_str, color]) in count_color.captures_iter(&l).map(|c| c.extract()) {
            let count: i32 = count_str.parse().unwrap();
            if !mins.contains_key(color) || count > *mins.get(color).unwrap() {
                mins.insert(color, count);
            }
        }
        result += mins.values().fold(1, |acc, v| acc * v);
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
