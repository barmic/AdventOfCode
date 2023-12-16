use std::env;
use std::io::{self, BufRead};

enum Validation {
    Valid,
    Fail,
    //Partial {first: usize, count: u32},
    PartialGuess {next: char, times: usize, nnext: Option<char>},
    PartialUnknow,
}

fn partial_valid(guess: &String, constraints: &Vec<i32>) -> Validation {
    let mut groupe_idx = 0;
    let mut current_size: i32 = 0;
    let mut first_wild = 0;
    let mut is_total = true;
    let mut i = 0;
    let mut partial = false;

    for ele in guess.chars() {
        let group_size = constraints.get(groupe_idx).unwrap_or(&0);
        match ele {
            '#' => {
                current_size += 1;
                if group_size < &current_size {
                    return Validation::Fail;
                }
            },
            '.' => {
                if current_size <= 0 {
                    continue;
                }
                if is_total && group_size != &current_size {
                    return Validation::Fail;
                } else if !is_total && group_size < &current_size {
                    return Validation::Fail;
                }
                current_size = 0;
                groupe_idx += 1;
            },
            '?' => {
                if is_total {
                    first_wild = i;
                    is_total = false;
                }
                if current_size <= 0 {
                    continue;
                }
                let missing = (group_size - &current_size) as usize;
                if missing > 0 {
                    let mut times = 1;
                    while guess.chars().nth((i + times) as usize) == Some('?') && times < missing {
                        times += 1;
                    }
                    if times < missing {
                        return Validation::Fail;
                    }
                    if times == missing && guess.chars().nth((i + times) as usize) == Some('?') {
                        return Validation::PartialGuess { next: '#', times, nnext: Some('.') };
                    }
                    return Validation::PartialGuess { next: '#', times, nnext: None };
                } else if missing == 0 {
                    return Validation::PartialGuess { next: '.', times: 1, nnext: None };
                }
                current_size = 0;
                groupe_idx += 1;
            },
            _ => panic!(),
        }
        i += 1; 
    }
    match (is_total, constraints.get(groupe_idx)) {
        (false, Some(n)) => if n > &current_size { Validation::PartialUnknow } else { Validation::Fail },
        (true, Some(n)) => if n > &current_size { Validation::Fail } else { Validation::Fail },
        (false, None) => if current_size == 0 { Validation::Valid } else { Validation::PartialUnknow },
        (true, None) => if current_size == 0 { Validation::Valid } else { Validation::Fail },
    }
}

fn valid(guess: &String, constraints: &Vec<i32>) -> bool {
    let mut i = 0;
    let mut current_size: i32 = 0;

    for ele in guess.chars() {
        match ele {
            '#' => current_size += 1,
            _ => {
                if current_size <= 0 {
                    continue;
                }
                let group_size = constraints.get(i).unwrap_or(&0);
                if group_size != &current_size {
                    return false;
                }
                current_size = 0;
                i += 1;
            }
        }
    }
    match constraints.get(i) {
        Some(n) => n == &current_size && i == constraints.len() - 1,
        None => current_size == 0,
    }
}

fn nb_guess(pattern: &String) -> usize {
    return pattern.match_indices('?').count();
}

/*fn tree_count_solutions(guess: &String, constraints: &Vec<i32>) -> u128 {
    return match partial_valid(guess, constraints) {
        Validation::Valid => 1,
        Validation::Fail => 0,
        Validation::PartialGuess { next, times, nnext } => {
            println!("guess {} {}", &next, guess);
            let mut deepth = &guess.clone();
            deepth = &str::replacen(&deepth, "?", &String::from(next), times);
            if nnext == Some('.') {
                deepth = &str::replacen(&deepth, "?", ".", 1);
            }
            return tree_count_solutions(&str::replacen(&deepth, "?", "#", 1), constraints)
                + tree_count_solutions(&str::replacen(&deepth, "?", ".", 1), constraints);
        },
        Validation::PartialUnknow => {
            println!("unknown {}", guess);
            return tree_count_solutions(&str::replacen(&guess, "?", "#", 1), constraints)
                + tree_count_solutions(&str::replacen(&guess, "?", ".", 1), constraints);
        },
    };
    / *if nb_guess(guess) == 0 {
        return if valid(guess, constraints) { 1 } else { 0 };
    }
    if !partial_valid(guess, constraints) {
        return 0;
    }
    return tree_count_solutions(&str::replacen(&guess, "?", "#", 1), constraints)
     + tree_count_solutions(&str::replacen(&guess, "?", ".", 1), constraints);* /
}*/

enum Paths {
    Success,
    Fail,
    Partial {left: String, right: String},
}

fn next_paths(guess: &String, constraints: &Vec<i32>) -> Paths {
    return match partial_valid(guess, constraints) {
        Validation::Valid => Paths::Success,
        Validation::Fail => Paths::Fail,
        Validation::PartialGuess { next, times, nnext } => {
            println!("guess {}*{} {} {}", &next, times, nnext.unwrap_or('_'), guess);
            let mut deepth = guess.clone();
            deepth = str::replacen(&guess, "?", &String::from(next), times);
            if nnext == Some('.') {
                deepth = str::replacen(&deepth, "?", ".", 1);
            }
            return Paths::Partial {
                left: str::replacen(&deepth, "?", "#", 1),
                right: str::replacen(&deepth, "?", ".", 1),
            };
        },
        Validation::PartialUnknow => {
            println!("unknown {}", guess);
            return Paths::Partial {
                left: str::replacen(&guess, "?", "#", 1),
                right: str::replacen(&guess, "?", ".", 1),
            };
        },
    };/*
    if nb_guess(guess) == 0 {
        if valid(guess, constraints) { 
            //println!("+{}", guess);
            return Paths::Success
        } else {
            //println!("-{}", guess);
            return Paths::Fail;
        }
    }
    if !partial_valid(guess, constraints) {
        //println!("_{}", guess);
        return Paths::Fail;
    }
    return Paths::Partial {
        left: str::replacen(&guess, "?", "#", 1),
        right: str::replacen(&guess, "?", ".", 1),
    };*/
}

fn tree_count_solutions_iter(guess: String, constraints: &Vec<i32>) -> u128 {
    let mut paths = vec![guess.clone()];

    let mut result = 0;
    while !paths.is_empty() {
        //println!("={}", paths.len());
        let current_path = paths.pop().unwrap();
        match next_paths(&current_path, constraints) {
            Paths::Success => result += 1,
            Paths::Fail => {},
            Paths::Partial { left, right } => {
                paths.push(left.to_owned());
                paths.push(right.to_owned());
            }
        }
    }

    dbg!(guess.clone(), result);
    return result;
}

fn part1() -> u128 {
    let mut result = 0;
    for line in io::stdin().lock().lines() {
        let vatefairefoutre = line.unwrap();
        let mut parts = vatefairefoutre.split(' ');
        let pattern = parts.next().unwrap().to_string();
        let constraints: Vec<i32> = parts.next().unwrap().split(',').map(|v| v.parse::<i32>().unwrap()).collect();

        result += tree_count_solutions_iter(pattern, &constraints);
        //break;
    }
    return result;
}

fn part2() -> u128 {
    let mut result = 0;
    for line in io::stdin().lock().lines() {
        let vatefairefoutre = line.unwrap();
        let mut parts = vatefairefoutre.split(' ');
        let mut pattern = parts.next().unwrap().to_string();
        let mut constr = parts.next().unwrap().to_string();
        let rep = pattern.clone();
        let rep2 = constr.clone();
        for _ in 0..4 {
            pattern.push('?');
            pattern.push_str(&rep);
            constr.push(',');
            constr.push_str(&rep2.clone());
        }
        let constraints: Vec<i32> = constr.split(',').map(|v| v.parse::<i32>().unwrap()).collect();

        result += tree_count_solutions_iter(pattern, &constraints);
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
