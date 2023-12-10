use regex::Regex;
use std::cmp::Ordering;
use std::env;
use std::io::{self, BufRead};

static CARDS: &str = "23456789TJQKA";

fn hand_type(hand: &String) -> u8 {
    let mut counters = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
    for c in hand.chars().into_iter() {
        let i = CARDS.find(c).unwrap();
        counters[i] += 1;
    }
    counters.sort();
    counters.reverse();
    match (counters[0], counters[1]) {
        (5, _) => 6,
        (4, _) => 5,
        (3, 2) => 4,
        (3, _) => 3,
        (2, 2) => 2,
        (2, _) => 1,
        _ => 0,
    }
}

fn sort_hand(hand_a: &(String, u32), hand_b: &(String, u32)) -> Ordering {
    let type_a = hand_type(&hand_a.0);
    let type_b = hand_type(&hand_b.0);
    if type_a > type_b {
        return Ordering::Greater;
    } else if type_a < type_b {
        return Ordering::Less;
    }
    for (ca, cb) in hand_a.0.chars().into_iter().zip(hand_b.0.chars().into_iter()) {
        let v_a = CARDS.find(ca).unwrap();
        let v_b = CARDS.find(cb).unwrap();
        if v_a > v_b {
            return Ordering::Greater;
        } else if v_a < v_b {
            return Ordering::Less;
        }
    }
    return Ordering::Equal;
}

fn part1() -> u32 {
    let token_pattern = Regex::new(r"([^ ]+) (\d+)").unwrap();
    let mut hands: Vec<(String, u32)> = Vec::new();
    for line in io::stdin().lock().lines() {
        let l = line.unwrap();
        for (_, [hand, bid]) in token_pattern.captures_iter(&l).map(|c| c.extract()) {
            hands.push((hand.to_owned(), bid.parse::<u32>().unwrap()));
        }
    }

    hands.sort_by(|a, b| sort_hand(a, b));
    let mut result: u32 = 0;
    for (idx, (_, bid)) in hands.iter().enumerate() {
        result += ((idx + 1) as u32) * bid;
    }
    return result;
}

static CARDS_JOCKER: &str = "J23456789TQKA";

fn hand_type_jocker(hand: &String) -> u8 {
    let mut counters = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
    for c in hand.chars().into_iter() {
        let i = CARDS_JOCKER.find(c).unwrap();
        counters[i] += 1;
    }
    let mut values = (0, 0);
    for i in 1..counters.len() {
        if counters[i] > values.0 {
            values = (counters[i], values.0);
        } else if counters[i] > values.1 {
            values = (values.0, counters[i]);
        }
    }
    let j = counters[0];
    values = (values.0 + j, values.1);
    match values {
        (5, _) => 6,
        (4, _) => 5,
        (3, 2) => 4,
        (3, _) => 3,
        (2, 2) => 2,
        (2, _) => 1,
        _ => 0,
    }
}

fn sort_hand_jocker(hand_a: &(String, u32), hand_b: &(String, u32)) -> Ordering {
    let type_a = hand_type_jocker(&hand_a.0);
    let type_b = hand_type_jocker(&hand_b.0);
    if type_a > type_b {
        return Ordering::Greater;
    } else if type_a < type_b {
        return Ordering::Less;
    }
    for (ca, cb) in hand_a.0.chars().into_iter().zip(hand_b.0.chars().into_iter()) {
        let v_a = CARDS_JOCKER.find(ca).unwrap();
        let v_b = CARDS_JOCKER.find(cb).unwrap();
        if v_a > v_b {
            return Ordering::Greater;
        } else if v_a < v_b {
            return Ordering::Less;
        }
    }
    return Ordering::Equal;
}

fn part2() -> u32 {
    let token_pattern = Regex::new(r"([^ ]+) (\d+)").unwrap();
    let mut hands: Vec<(String, u32)> = Vec::new();
    for line in io::stdin().lock().lines() {
        let l = line.unwrap();
        for (_, [hand, bid]) in token_pattern.captures_iter(&l).map(|c| c.extract()) {
            hands.push((hand.to_owned(), bid.parse::<u32>().unwrap()));
        }
    }

    hands.sort_by(|a, b| sort_hand_jocker(a, b));
    let mut result: u32 = 0;
    for (idx, (_, bid)) in hands.iter().enumerate() {
        result += ((idx + 1) as u32) * bid;
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
