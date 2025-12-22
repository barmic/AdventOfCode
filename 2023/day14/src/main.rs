use std::env;
use std::io::{self, BufRead};
use std::collections::HashMap;

#[derive(Debug, Hash, Clone, Copy)]
struct Case {
    x: usize,
    y: usize,
    movable: bool,
}
impl PartialEq for Case {
    fn eq(&self, other: &Self) -> bool {
        self.x == other.x && self.y == other.y && self.movable && other.movable
    }
}
impl Eq for Case {}

fn north(map: &mut Vec<Case>) {
    map.sort_by(|a, b| a.y.partial_cmp(&b.y).unwrap());

    let mut offset: HashMap<usize, usize> = HashMap::new();

    for obj in map.iter_mut() {
        if obj.movable {
            let pos = offset.get(&obj.x).unwrap_or(&0);
            obj.y = *pos;
        }
        offset.insert(obj.x, obj.y + 1);
    }
}

fn west(map: &mut Vec<Case>) {
    map.sort_by(|a, b| a.x.partial_cmp(&b.x).unwrap());

    let mut offset: HashMap<usize, usize> = HashMap::new();

    for obj in map.iter_mut() {
        if obj.movable {
            let pos = offset.get(&obj.y).unwrap_or(&0);
            obj.x = *pos;
        }
        offset.insert(obj.y, obj.x + 1);
    }
}

fn south(map: &mut Vec<Case>, size: usize) {
    map.sort_by(|a, b| a.y.partial_cmp(&b.y).unwrap());
    map.reverse();

    let mut offset: HashMap<usize, usize> = HashMap::new();

    for obj in map.iter_mut() {
        if obj.movable {
            let pos = match offset.get(&obj.x) {
                Some(n) => *n,
                None => size - 1,
            };
            obj.y = pos;
        }
        offset.insert(obj.x, obj.y - 1);
    }
}

fn east(map: &mut Vec<Case>, size: usize) {
    map.sort_by(|a, b| a.x.partial_cmp(&b.x).unwrap());
    map.reverse();

    let mut offset: HashMap<usize, usize> = HashMap::new();

    for obj in map.iter_mut() {
        if obj.movable {
            let pos = match offset.get(&obj.y) {
                Some(n) => *n,
                None => size - 1,
            };
            obj.x = pos;
        }
        offset.insert(obj.y, obj.x - 1);
    }
}

fn part1() -> u32 {
    let mut weigths: Vec<u32> = Vec::new();
    let mut result : Vec<u32> = Vec::new();
    let mut nb_lines = 0;
    for (no, line) in io::stdin().lock().lines().enumerate() {
        nb_lines += 1;
        let l = line.unwrap();
        if weigths.is_empty() {
            for _ in 0..l.len() {
                weigths.push(0);
            }
        }
        for (i, c) in l.chars().enumerate() {
            match c {
                'O' => {
                    let weight = weigths.get(i).unwrap();
                    result.push(*weight);
                    weigths[i] = weight + 1;
                },
                '#' => weigths[i] = (no as u32) + 1,
                _ => {},
            }
        }
    }
    return result.iter().map(|l| nb_lines - l).sum();
}

fn guess_interval(a: &Vec<usize>) -> Option<usize> {
    if a.len() <= 5 {
        return None;
    }
    let last = a.last().unwrap();
    let interv: Vec<usize> = a.iter().enumerate().filter(|(_, i)| i == &last).map(|(i, _)| i).collect();
    let last_idx = interv.len() - 1;
    let first = interv.get(last_idx).zip(interv.get(last_idx - 1)).map(|(b, c)| b - c);
    let second = interv.get(last_idx - 1).zip(interv.get(last_idx - 2)).map(|(b, c)| b - c);
    return if first == second {first}
        else {None};
}

fn found(a: &Vec<usize>, interval_size: usize, iter_target: usize) -> usize {
    let offset = (iter_target - a.len() - 1) % interval_size;
    let interval: Vec<usize> = (0..interval_size).map(|i| *(a.get(a.len() - interval_size + i)).unwrap()).collect();
    return *interval.get(offset).unwrap();
}

fn part2(nb_iter: usize) -> usize {
    let mut map: Vec<Case> = Vec::new();
    let mut nb_lines = 0;
    let mut nb_cols = 0;
    for (y, line) in io::stdin().lock().lines().enumerate() {
        nb_lines += 1;
        let l = line.unwrap();
        if nb_cols == 0 {
            nb_cols = l.len();
        }
        for (x, c) in l.chars().enumerate() {
            match c {
                'O' => map.push(Case { x, y, movable: true }),
                '#' => map.push(Case { x, y, movable: false }),
                _ => {},
            }
        }
    }

    let mut previous: Vec<usize> = Vec::new();
    let mut interval_size: usize = 0;
    for i in 0..nb_iter {
        north(&mut map);
        west(&mut map);
        south(&mut map, nb_lines);
        east(&mut map, nb_cols);
        let current: usize = map.iter().map(|c| if c.movable { nb_lines - c.y } else { 0 }).sum();
        previous.push(current);

        match guess_interval(&previous) {
            Some(isize) => {
                if previous.len() >= 2 * isize {
                    let valid = (0..isize).all(|j| previous.get(i - j).unwrap() == previous.get(i - j - isize).unwrap());
                    if valid {
                        interval_size = isize;
                        break;
                    }
                }
            },
            None => {},
        }
    }
    return found(&previous, interval_size, nb_iter);
}

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() > 1 && args[1] == "part2" {
        println!("Part 2: {}", part2(1_000_000_000));
    } else {
        println!("Part 1: {}", part1());
    }
}
