use std::collections::HashSet;
use std::env;
use std::io::{self, BufRead};

fn part1() -> i32 {
    let mut result = 0;
    let base: i32 = 2;
    for line in io::stdin().lock().lines() {
        let mut succ = 0;
        let mut win = HashSet::new();
        let mut phase1 = true;
        for cap in line.unwrap().split(' ') {
            if cap == "|" {
                phase1 = false;
                continue;
            }
            let value = cap.trim().parse::<i32>();
            match value {
                Err(_) => continue,
                Ok(val) => {
                    if phase1 {
                        win.insert(val);
                    } else if win.contains(&val) {
                        succ += 1;
                    }
                }
            }
        }
        if succ > 0 {
            result += base.pow(succ - 1);
        }
    }
    return result;
}

fn count(table: &Vec<std::ops::RangeInclusive<usize>>, cards: Vec<usize>) -> usize {
    if cards.len() == 0 {
        return 0;
    } else {
        let mut new_cards: Vec<usize> = Vec::new();
        for current in cards.iter() {
            let part_new_cards = table.get(*current);
            for new_card in part_new_cards.unwrap().clone().into_iter() {
                new_cards.push(new_card);
            }
        }
        return cards.len() + count(table, new_cards);
    }
}

fn part2() -> i32 {
    let mut table = Vec::new();
    // initialize
    for line in io::stdin().lock().lines() {
        let mut succ = 0;
        let mut win = HashSet::new();
        let mut phase1 = true;
        for cap in line.unwrap().split(' ') {
            if cap == "|" {
                phase1 = false;
                continue;
            }
            let value = cap.trim().parse::<i32>();
            match value {
                Err(_) => continue,
                Ok(val) => {
                    if phase1 {
                        win.insert(val);
                    } else if win.contains(&val) {
                        succ += 1;
                    }
                }
            }
        }
        if succ > 0 {
            table.push(table.len() + 1..=(table.len() + succ));
        } else {
            table.push(std::ops::RangeInclusive::new(1, 0));
        }
    }
    return count(&table, (0..table.len()).collect()) as i32;
}

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() > 1 && args[1] == "part2" {
        println!("Part 2: {}", part2());
    } else {
        println!("Part 1: {}", part1());
    }
}
