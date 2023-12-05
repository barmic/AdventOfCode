use regex::Regex;
use std::cmp;
use std::collections::HashMap;
use std::env;
use std::io::{self, BufRead};
use std::ops::Range;

fn mapping(value: i64, mapper: &HashMap<Range<i64>, i64>) -> i64 {
    for (ran, delta) in mapper {
        if ran.contains(&value) {
            return value + delta;
        }
    }
    return value;
}

fn mapping_range(value: Range<i64>, mapper: &HashMap<Range<i64>, i64>) -> Vec<Range<i64>> {
    let mut new_ranges = Vec::new();
    for ran in mapper.keys().sort_by(|a, b| a.start.cmp(&b.start)) {
        if ran.contains(&value.start) {
            let a = &value.start + delta;
            let b = cmp::min(&value.end, &ran.end) + delta;
            new_ranges.push(Range {start: cmp::min(a, b), end: cmp::max(a, b)});
        }
        if ran.contains(&value.end) {
            break;
        }
    }
    return if new_ranges.is_empty() { vec![value] } else { new_ranges };
}

fn simplify(values: &Vec<Range<i64>>) -> Vec<Range<i64>> {
    if values.len() < 2 {
        return values.clone();
    }
    let mut new_ranges = Vec::new();
    let mut plouf = values.clone();
    plouf.sort_by(|a, b| a.start.cmp(&b.start));
    let head = plouf.get(0).unwrap();
    let mut current = Range {start: head.start, end: head.end};
    for r in plouf {
        if (r.end <= current.start || current.start == r.end + 1) && current.end >= r.start {
            current = Range {start: current.start, end: r.end};
        } else {
            new_ranges.push(current);
            current = r;
        }
    }
    new_ranges.push(current);
    return new_ranges;
}

fn part2() -> i64 {
    let token_pattern = Regex::new(r"(\d+) (\d+)").unwrap();
    let map_pattern = Regex::new(r"(\d+) (\d+) (\d+)").unwrap();
    let mut seeds: Vec<Range<i64>> = Vec::new();
    let mut mapper = HashMap::new();
    for line in io::stdin().lock().lines() {
        let l = line.unwrap();
        if l.is_empty() {
            dbg!(&seeds);
            let a: Vec<Range<i64>> = seeds.iter().flat_map(|a| mapping_range(Range{start: a.start, end: a.end}, &mapper).into_iter()).collect();
            dbg!(&mapper);
            seeds = a;//simplify(&a);
            mapper.clear();
        } else if seeds.is_empty() {
            for (_, values) in token_pattern.captures_iter(&l).map(|c| c.extract()) {
                let [start, delta] = values.map(|str| str.parse::<i64>().unwrap());
                let ran = Range { start, end: start + delta };
                seeds.push(ran);
            }
            seeds = simplify(&seeds);
        } else {
            for (_, values) in map_pattern.captures_iter(&l).map(|c| c.extract()) {
                let [target, src, len] = values.map(|str| str.parse::<i64>().unwrap());
                let ran = Range { start: src, end: src + len };
                let delta = target - src;
                mapper.insert(ran, delta);
            }
        }
    }
    dbg!(&seeds);
    return seeds.iter().fold(i64::MAX, |a, b| cmp::min(a, b.start));
}

fn part1() -> i64 {
    let token_pattern = Regex::new(r"(\d+)").unwrap();
    let map_pattern = Regex::new(r"(\d+) (\d+) (\d+)").unwrap();
    let mut seeds: Vec<i64> = Vec::new();
    let mut mapper = HashMap::new();
    for line in io::stdin().lock().lines() {
        let l = line.unwrap();
        if l.is_empty() {
            seeds = seeds.iter().map(|a| mapping(*a, &mapper)).collect();
            mapper.clear();
        } else if seeds.is_empty() {
            for (_, [seed]) in token_pattern.captures_iter(&l).map(|c| c.extract()) {
                seeds.push(seed.parse::<i64>().unwrap());
            }
        } else {
            for (_, values) in map_pattern.captures_iter(&l).map(|c| c.extract()) {
                let [target, src, len] = values.map(|str| str.parse::<i64>().unwrap());
                let ran = Range { start: src, end: src + len };
                let delta = target - src;
                mapper.insert(ran, delta);
            }
        }
    }
    return *seeds.iter().reduce(|a, b| cmp::min(a, b)).unwrap();
}

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() > 1 && args[1] == "part2" {
        println!("Part 2: {}", part2());
    } else {
        println!("Part 1: {}", part1());
    }
}
