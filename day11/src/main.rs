use std::env;
use std::cmp;
use std::io::{self, BufRead};

fn part1() -> i32 {
    let mut map: Vec<(i32, i32)> = Vec::new();

    let mut lineno = 0;
    let mut max_x = 0;
    let mut max_y = 0;
    for (_, line) in io::stdin().lock().lines().enumerate() {
        let l = line.unwrap();
        for (galaxy, _) in l.match_indices('#') {
            max_x = cmp::max(max_x, galaxy as i32);
            max_y = cmp::max(max_y, lineno);
            map.push((galaxy as i32, lineno));
        }
        lineno += 1;
    }

    // expand x
    let mut expand = 0;
    let mut expanded_x: Vec<(i32, i32)> = Vec::new();
    for x in 0..=max_x {
        let mut found = false;
        for idx in 0..map.len() {
            let galaxy = map.get(idx).unwrap_or(&(-1, -1));
            if galaxy.0 == x {
                expanded_x.push((galaxy.0 + expand, galaxy.1));
                found = true;
            }
        }
        if !found {
            expand += 1;
        }
    }

    // expand y
    expand = 0;
    let mut full_expanded: Vec<(i32, i32)> = Vec::new();
    for y in 0..=max_y {
        let mut found = false;
        for idx in 0..expanded_x.len() {
            let galaxy = expanded_x.get(idx).unwrap_or(&(-1, -1));
            if galaxy.1 == y {
                full_expanded.push((galaxy.0, galaxy.1 + expand));
                found = true;
            }
        }
        if !found {
            expand += 1;
        }
    }

    let mut result = 0;
    for a in full_expanded.iter() {
        for b in full_expanded.iter() {
            result += distance(a, b);
        }
    }

    return result / 2;
}

fn distance(a: &(i32, i32), b: &(i32, i32)) -> i32 {
    return (a.0 - b.0).abs() + (a.1 - b.1).abs();
}

fn big_distance(a: &(u128, u128), b: &(u128, u128)) -> u128 {
    return cmp::max(a.0, b.0) - cmp::min(a.0,b.0)
     + cmp::max(a.1, b.1) - cmp::min(a.1,b.1);
}

fn part2(delta: u128) -> u128 {
    let mut map: Vec<(u128, u128)> = Vec::new();

    let mut lineno = 0;
    let mut max_x = 0;
    let mut max_y = 0;
    for (_, line) in io::stdin().lock().lines().enumerate() {
        let l = line.unwrap();
        for (galaxy, _) in l.match_indices('#') {
            max_x = cmp::max(max_x, galaxy as u128);
            max_y = cmp::max(max_y, lineno);
            map.push((galaxy as u128, lineno));
        }
        lineno += 1;
    }

    // expand x
    let mut expand = 0;
    let mut expanded_x: Vec<(u128, u128)> = Vec::new();
    for x in 0..=max_x {
        let mut found = false;
        for idx in 0..map.len() {
            let galaxy = map.get(idx).unwrap();
            if galaxy.0 == x {
                expanded_x.push((galaxy.0 + expand, galaxy.1));
                found = true;
            }
        }
        if !found {
            expand += delta;
        }
    }

    // expand y
    expand = 0;
    let mut full_expanded: Vec<(u128, u128)> = Vec::new();
    for y in 0..=max_y {
        let mut found = false;
        for idx in 0..expanded_x.len() {
            let galaxy = expanded_x.get(idx).unwrap();
            if galaxy.1 == y {
                full_expanded.push((galaxy.0, galaxy.1 + expand));
                found = true;
            }
        }
        if !found {
            expand += delta;
        }
    }

    let mut result = 0;
    for a in full_expanded.iter() {
        for b in full_expanded.iter() {
            result += big_distance(a, b);
        }
    }

    return result / 2;
}

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() > 1 && args[1] == "part2" {
        println!("Part 2: {}", part2(1_000_000-1));
    } else {
        println!("Part 1: {}", part1());
    }
}
