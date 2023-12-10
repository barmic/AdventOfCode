use regex::Regex;
use std::env;
use std::io::{self, BufRead};
use std::collections::HashMap;
use std::collections::HashSet;

struct Path {
    curr: usize,
    path: Vec<Edge>,
}

impl Iterator for Path {
    type Item = Edge;
    fn next(&mut self) -> Option<Self::Item> {
        let result = self.path.get(self.curr);
        self.curr = (self.curr + 1) % self.path.len();
        return result.copied();
    }
}

#[derive(Hash)]
struct DynDist {
    dir: Edge,
    node: String,
}
impl PartialEq for DynDist {
    fn eq(&self, other: &Self) -> bool {
        self.dir == other.dir && self.node == other.node
    }
}
impl Eq for DynDist {}

fn path(p: Vec<Edge>) -> Path {
    Path { curr: 0, path: p }
}

#[derive(Copy, Clone, PartialEq, Hash)]
enum Edge {
    L,
    R,
}

fn part1() -> u32 {
    let token_pattern = Regex::new(r"([^ ]+) = \(([^ ]+), ([^ ]+)\)").unwrap();
    let mut road: Vec<Edge> = Vec::new();
    let mut map: HashMap<String, (String, String)> = HashMap::new();
    for (idx, line) in io::stdin().lock().lines().enumerate() {
        if idx == 0 {
            road = line.unwrap().chars()
                .map(|c| if c == 'L' { Edge::L } else { Edge::R })
                .collect();
        } else {
            let l = line.unwrap();
            for (_, [node, left, right]) in token_pattern.captures_iter(&l).map(|c| c.extract()) {
                map.insert(node.to_owned(), (left.to_owned(), right.to_owned()));
            }
        }
    }
    let mut a = path(road).into_iter();

    let mut result = 0;
    let mut current = "AAA".to_string();
    while current != "ZZZ" && map.contains_key(&current) {
        let node = map.get(&current).unwrap();
        current = match a.next() {
            Some(Edge::L) => node.0.clone(),
            Some(Edge::R) => node.1.clone(),
            None => panic!("this is a terrible mistake!"),
        };
        result += 1;
    }
    
    return result;
}

fn part2() -> u64 {
    let token_pattern = Regex::new(r"([^ ]+) = \(([^ ]+), ([^ ]+)\)").unwrap();
    let mut road: Vec<Edge> = Vec::new();
    let mut map: HashMap<String, (String, String)> = HashMap::new();
    for (idx, line) in io::stdin().lock().lines().enumerate() {
        if idx == 0 {
            road = line.unwrap().chars()
                .map(|c| if c == 'L' { Edge::L } else { Edge::R })
                .collect();
        } else {
            let l = line.unwrap();
            for (_, [node, left, right]) in token_pattern.captures_iter(&l).map(|c| c.extract()) {
                map.insert(node.to_owned(), (left.to_owned(), right.to_owned()));
            }
        }
    }
    let mut a = path(road).into_iter();

    let mut steps: HashMap<DynDist, u32> = HashMap::new();

    let mut result: u64 = 0;
    let mut currents: HashSet<String> = map.keys().cloned().into_iter().filter(|k| k.ends_with("A")).collect();
    /*for ele in currents.iter() {
        steps.insert(DynDist { dir: Edge::L, node: ele.clone() }, 0);
        steps.insert(DynDist { dir: Edge::R, node: ele.clone() }, 0);
    }*/
    while !is_finish(&currents) {
        let mut next_currents = HashSet::new();
        let dir = match a.next() {
            Some(d) => d,
            None => panic!("this is a terrible mistake!"),
        };
        
        for curr in currents.iter() {
            let node = map.get(curr).unwrap();
            let next = match dir {
                Edge::L => node.0.clone(),
                Edge::R => node.1.clone(),
            };
            next_currents.insert(next);
        }
        currents = next_currents;
        result += 1;
    }
    
    return result;
}

fn is_finish(currents: &HashSet<String>) -> bool {
    currents.iter().all(|curr| curr.ends_with("Z"))
}

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() > 1 && args[1] == "part2" {
        println!("Part 2: {}", part2());
    } else {
        println!("Part 1: {}", part1());
    }
}
