use regex::Regex;
use std::env;
use std::io::{self, BufRead};

fn course(time: u64, distance: u64) -> u64 {
    let delta = (time * time) - (4 * distance);
    if delta <= 0 {
        return 0;
    } else {
        let sqrt = (delta as f64).sqrt();
        let t = time as f64;
        let x1 = (-t + sqrt) / -2f64;
        let x2 = (-t - sqrt) / -2f64;

        let mut min = x1.abs().ceil() as u64;
        let mut max = x2.abs().floor() as u64;
        if (time - min) * min == distance {
            min += 1;
        }
        if (time - max) * max == distance {
            max -= 1;
        }
        return max - min + 1;
    }
}

fn part1() -> u64 {
    let token_pattern = Regex::new(r"(\d+)").unwrap();
    let mut times: Vec<u64> = Vec::new();
    let mut distances: Vec<u64> = Vec::new();
    for line in io::stdin().lock().lines() {
        let l = line.unwrap();
        if l.starts_with("Time:") {
            for (_, [seed]) in token_pattern.captures_iter(&l).map(|c| c.extract()) {
                times.push(seed.parse::<u64>().unwrap());
            }
        } else if l.starts_with("Distance:") {
            for (_, [seed]) in token_pattern.captures_iter(&l).map(|c| c.extract()) {
                distances.push(seed.parse::<u64>().unwrap());
            }
        }
    }
    let mut result: u64 = 1;
    
    for (time, distance) in times.iter().zip(distances.iter()) {
        let ways = course(*time, *distance);
        result *= ways;
    }
    return result;
}

fn part2() -> u64 {
    let token_pattern = Regex::new(r"(\d+)").unwrap();
    let mut times = "".to_owned();
    let mut distances = "".to_owned();
    for line in io::stdin().lock().lines() {
        let l = line.unwrap();
        if l.starts_with("Time:") {
            for (_, [seed]) in token_pattern.captures_iter(&l).map(|c| c.extract()) {
                times.push_str(seed);
            }
        } else if l.starts_with("Distance:") {
            for (_, [seed]) in token_pattern.captures_iter(&l).map(|c| c.extract()) {
                distances.push_str(seed);
            }
        }
    }

    let time = times.parse::<u64>().unwrap();
    let distance = distances.parse::<u64>().unwrap();
    
    return course(time, distance);
}

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() > 1 && args[1] == "part2" {
        println!("Part 2: {}", part2());
    } else {
        println!("Part 1: {}", part1());
    }
}
