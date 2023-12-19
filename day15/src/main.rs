use regex::Regex;
use std::env;
use std::io::{self, BufRead};

fn hash(input: String) -> usize {
    return input
        .chars()
        .fold(0, |curr, c| ((curr + c as usize) * 17) % 256);
}

fn part1() -> usize {
    let mut result: usize = 0;
    for line in io::stdin().lock().lines() {
        for token in line.unwrap().split(",") {
            result += hash(token.to_owned());
        }
    }
    return result;
}

fn count(boxes: &[Vec<(String, u32)>; 256]) -> usize {
    let mut result = 0;
    for (no, content) in boxes.iter().enumerate() {
        if content.is_empty() {
            continue;
        }
        result += (no + 1)
            * content
                .iter()
                .enumerate()
                .map(|(n, (_, f))| (*f as usize) * (n + 1))
                .sum::<usize>();
        dbg!(result);
    }
    return result;
}

fn part2() -> usize {
    let token_pattern = Regex::new(r"(.+?)(=\d+|-),?").unwrap();
    let mut boxes: [Vec<(String, u32)>; 256] = std::array::from_fn(|_| Vec::new());
    for line in io::stdin().lock().lines().map(|f| f.unwrap()) {
        for (_, [label_str, cmd]) in token_pattern.captures_iter(&line).map(|c| c.extract()) {
            let label = hash(label_str.to_owned());
            match cmd.chars().nth(0) {
                Some('=') => {
                    let focal = cmd[1..].parse::<u32>().unwrap();
                    let position = boxes[label].iter().position(|lens| lens.0 == label_str);
                    if position.is_some() {
                        boxes[label].get_mut(position.unwrap()).unwrap().1 = focal;
                    } else {
                        boxes[label].push((label_str.to_string(), focal));
                    }
                }
                Some('-') => {
                    let position = boxes[label].iter().position(|lens| lens.0 == label_str);
                    if position.is_some() {
                        boxes[label].remove(position.unwrap());
                    }
                }
                _ => panic!(),
            }
        }
    }
    return count(&boxes);
}

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() > 1 && args[1] == "part2" {
        println!("Part 2: {}", part2());
    } else {
        println!("Part 1: {}", part1());
    }
}
