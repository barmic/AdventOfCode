use std::env;
use std::io::{self, BufRead};

fn column(lines: &Vec<i32>, columnno: usize) -> i32 {
    let mut result = 0;
    for (i, line) in lines.iter().enumerate() {
        if (line >> columnno) & 1 == 1 {
            result = result | (1 << i);
        }
    }
    return result;
}

fn check_columns(lines: &Vec<i32>, columns: usize) -> Option<usize> {
    let cols: Vec<i32> = (0..columns)
        .into_iter().map(|i| column(&lines, i)).collect();
    return check_vector(&cols);
}

fn check_lines(lines: &Vec<i32>) -> Option<usize> {
    return check_vector(lines).map(|v| v * 100);
}

fn check_vector(data: &Vec<i32>) -> Option<usize> {
    for no in 0..data.len() {
        let foo: Vec<(usize, usize)> = (0..=no).rev().zip(no+1..data.len()).collect();
        let sym = !foo.is_empty() && foo.iter().all(|(a, b)| {
            let a_data = data.get(*a);
            let b_data = data.get(*b);
            return a_data == b_data;
        });
        if sym {
            return Some(no + 1);
        }
    }
    return None;
}

fn check_columns_with_max_one(lines: &Vec<i32>, columns: usize) -> Option<usize> {
    let cols: Vec<i32> = (0..columns)
        .into_iter().map(|i| column(&lines, i)).collect();
    return check_vector_with_max_one(&cols);
}

fn check_lines_with_max_one(lines: &Vec<i32>) -> Option<usize> {
    return check_vector_with_max_one(lines).map(|v| v * 100);
}

fn check_vector_with_max_one(data: &Vec<i32>) -> Option<usize> {
    for no in 0..data.len() {
        let foo: Vec<(usize, usize)> = (0..=no).rev().zip(no+1..data.len()).collect();
        if foo.is_empty() {
            continue;
        }
        let sym: i32 = foo.iter().map(|(a, b)| {
            let a_data = data.get(*a).unwrap();
            let b_data = data.get(*b).unwrap();
            let c = a_data ^ b_data;
            if c == 0 {
                return 0;
            } else if (c & (c - 1)) == 0 {
                return 1;
            } else {
                return 2;
            }
        }).sum();
        if sym == 1{
            return Some(no + 1);
        }
    }
    return None;
}

fn load(line: &String) -> i32 {
    let mut result = 0;
    for (i, c) in line.chars().enumerate() {
        if c == '#' {
            result = result | (1 << i);
        }
    }
    return result;
}

fn part1() -> usize {
    let mut lines: Vec<i32> = Vec::new();
    let mut result = 0;
    let mut colcheck = true;
    let mut line_size: usize = 0;
    for line in io::stdin().lock().lines() {
        let l = line.unwrap();
        if l.is_empty() {
            let v = match check_lines(&lines) {
                Some(n) => n,
                None => check_columns(&lines, line_size).unwrap(),
            };
            result += v;
            lines.clear();
            colcheck = !colcheck;
        } else {
            line_size = l.len();
            let lvalue = load(&l);
            lines.push(lvalue);
        }
    }
    let v = match check_lines(&lines) {
        Some(n) => Some(n),
        None => check_columns(&lines, line_size),
    };
    return result + v.unwrap();
}

fn part2() -> usize {
    let mut lines: Vec<i32> = Vec::new();
    let mut result = 0;
    let mut colcheck = true;
    let mut line_size: usize = 0;
    for line in io::stdin().lock().lines() {
        let l = line.unwrap();
        if l.is_empty() {
            let v = match check_lines_with_max_one(&lines) {
                Some(n) => n,
                None => check_columns_with_max_one(&lines, line_size).unwrap(),
            };
            println!("{}", v);
            result += v;
            lines.clear();
            colcheck = !colcheck;
        } else {
            line_size = l.len();
            let lvalue = load(&l);
            lines.push(lvalue);
        }
    }
    let v = match check_lines_with_max_one(&lines) {
        Some(n) => Some(n),
        None => check_columns_with_max_one(&lines, line_size),
    };
    return result + v.unwrap();
}

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() > 1 && args[1] == "part2" {
        println!("Part 2: {}", part2());
    } else {
        println!("Part 1: {}", part1());
    }
}
