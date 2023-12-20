use itertools::Itertools;
use std::cmp::Ordering;
use std::env;
use std::io::{self, BufRead};
use std::collections::HashSet;

#[derive(Debug, Clone, PartialEq, Eq, Copy)]
enum Direction {
    N,
    S,
    E,
    W,
}

#[derive(Debug, Clone, PartialEq, Eq)]
enum State {
    Pipe,
    Minus,
    BottomLeft,
    UpRight,
    Energize { dir: Direction },
}

#[derive(Debug, Clone)]
struct Case {
    x: usize,
    y: usize,
    state: State,
}

fn column(map: &Vec<Case>, x: usize, origin: usize, limit: usize) -> Option<Case> {
    return match origin.cmp(&limit) {
        Ordering::Equal => None,
        Ordering::Greater => {
            let ignored = vec![
                State::Pipe,
                State::Energize { dir: Direction::N },
                State::Energize { dir: Direction::E },
                State::Energize { dir: Direction::W },
            ];
            return map.iter()
                .filter(|c| {
                    c.x == x && (limit..origin).contains(&c.y) && !ignored.contains(&c.state)
                })
                .max_by_key(|c| c.y)
                .cloned().or(Some(energized_case(x, limit, Direction::E)));
        }
        Ordering::Less => {
            let ignored = vec![
                State::Pipe,
                State::Energize { dir: Direction::S },
                State::Energize { dir: Direction::E },
                State::Energize { dir: Direction::W },
            ];
            return map.iter()
                .filter(|c| {
                    c.x == x && ((origin + 1)..=limit).contains(&c.y) && !ignored.contains(&c.state)
                })
                .min_by_key(|c| c.y)
                .cloned().or(Some(energized_case(x, limit, Direction::E)));
        }
    };
}

fn line(map: &Vec<Case>, y: usize, origin: usize, limit: usize) -> Option<Case> {
    dbg!("dana", origin, limit);
    return match origin.cmp(&limit) {
        Ordering::Equal => None,
        Ordering::Greater => {
            let ignored = vec![
                State::Minus,
                State::Energize { dir: Direction::N },
                State::Energize { dir: Direction::S },
                State::Energize { dir: Direction::E },
            ];
            dbg!("sandrine");
            return map
                .iter()
                .filter(|c| {
                    c.y == y && (limit..origin).contains(&c.x) && !ignored.contains(&c.state)
                })
                .max_by_key(|c| c.x)
                .cloned().or(Some(energized_case(limit, y, Direction::N)));
        }
        Ordering::Less => {
            let ignored = vec![
                State::Minus,
                State::Energize { dir: Direction::N },
                State::Energize { dir: Direction::S },
                State::Energize { dir: Direction::W },
            ];
            return map.iter()
                .filter(|c| {
                    c.y == y && ((origin + 1)..=limit).contains(&c.x) && !ignored.contains(&c.state)
                })
                .min_by_key(|c| c.x)
                .cloned().or(Some(energized_case(limit, y, Direction::N)))
        }
    };
}

fn energized_case(x: usize, y: usize, direction: Direction) -> Case {
    return Case {
        x,
        y,
        state: State::Energize { dir: direction },
    };
}

fn energized(start_x: usize, start_y: usize, end: &Case) -> Vec<Case> {
    return match (start_x.cmp(&end.x), start_y.cmp(&end.y)) {
        (Ordering::Greater, _) => (end.x..start_x)
            .into_iter()
            .map(|x| energized_case(x, start_y, Direction::W))
            .collect(),
        (Ordering::Less, _) => (start_x..=end.x)
            .into_iter()
            .map(|x| energized_case(x, start_y, Direction::E))
            .collect(),
        (_, Ordering::Greater) => (end.y..start_y)
            .into_iter()
            .map(|y| energized_case(start_x, y, Direction::N))
            .collect(),
        (_, Ordering::Less) => (start_y..=end.y)
            .into_iter()
            .map(|y| energized_case(start_x, y, Direction::S))
            .collect(),
        _ => vec![],
    };
}

fn run(start: (usize, usize, Direction), map: &mut Vec<Case>, x_max: usize, y_max: usize) {
    let mut positions: Vec<(usize, usize, Direction)> = Vec::new();
    positions.push(start);
    while !positions.is_empty() {
        //dbg!(&positions);
        let current = positions.remove(0);
        let next = match current.2 {
            Direction::E => line(map, current.1, current.0, x_max),
            Direction::W => line(map, current.1, current.0, 0),
            Direction::N => column(map, current.0, current.1, 0),
            Direction::S => column(map, current.0, current.1, y_max),
        };
        dbg!(&current, &next);
        if next.is_some() {
            //display(&map, x_max, y_max);
            display_only_nrj(&map, x_max, y_max);
            let next_position = next.unwrap();
            let mut energized = energized(current.0, current.1, &next_position);
            dbg!(current, &next_position, &energized);
            if !energized.is_empty() {
                map.append(&mut energized);
                let directions: Vec<Direction> = match (next_position.state, current.2) {
                    (State::BottomLeft, Direction::E) => vec![Direction::N],
                    (State::BottomLeft, Direction::W) => vec![Direction::S],
                    (State::BottomLeft, Direction::N) => vec![Direction::E],
                    (State::BottomLeft, Direction::S) => vec![Direction::W],

                    (State::UpRight, Direction::E) => vec![Direction::S],
                    (State::UpRight, Direction::W) => vec![Direction::N],
                    (State::UpRight, Direction::N) => vec![Direction::W],
                    (State::UpRight, Direction::S) => vec![Direction::E],

                    (State::Pipe, _) => vec![Direction::N, Direction::S],

                    (State::Minus, _) => vec![Direction::E, Direction::W],

                    (State::Energize { .. }, _) => vec![],
                };
                let mut all_next: Vec<(usize, usize, Direction)> = directions
                    .iter()
                    .map(|d| (next_position.x, next_position.y, *d))
                    .collect();
                positions.append(&mut all_next);
            }
        }
    }
}

fn display_only_nrj(map: &Vec<Case>, width: usize, height: usize) {
    let priority = [State::Minus, State::BottomLeft, State::UpRight, State::Pipe];
    for y in 0..height {
        for x in 0..width {
            let nrj = !map
                .iter()
                .filter(|case| case.x == x && case.y == y && !priority.contains(&case.state))
                .collect::<Vec<&Case>>()
                .is_empty();
            print!("{}", if nrj { '#' } else { '.' });
        }
        println!("");
    }
}

fn display(map: &Vec<Case>, width: usize, height: usize) {
    let priority = [State::Minus, State::BottomLeft, State::UpRight, State::Pipe];
    for y in 0..height {
        for x in 0..width {
            let states: Vec<&State> = map
                .iter()
                .filter(|case| case.x == x && case.y == y)
                .map(|case| &case.state)
                .sorted_by_key(|state| priority.iter().position(|s| s == *state).unwrap_or(12))
                .collect();
            let c: String = match states.first() {
                None => ".".to_string(),
                Some(State::Minus) => "-".to_string(),
                Some(State::Pipe) => "|".to_string(),
                Some(State::BottomLeft) => "/".to_string(),
                Some(State::UpRight) => "\\".to_string(),
                Some(State::Energize { dir }) => display_nrj(dir, states.len()),
            };
            print!("{}", c);
        }
        println!("");
    }
}

fn display_nrj(dir: &Direction, len: usize) -> String {
    if len == 1 {
        return match dir {
            Direction::E => ">".to_string(),
            Direction::W => "<".to_string(),
            Direction::N => "^".to_string(),
            Direction::S => "v".to_string(),
        };
    } else {
        return len.to_string();
    }
}

fn part1() -> usize {
    let mut map: Vec<Case> = Vec::new();
    let mut x_max: usize = 0;
    let mut y_max: usize = 0;
    for (line, cases) in io::stdin().lock().lines().enumerate() {
        y_max += 1;
        x_max = cases.as_ref().unwrap().len();
        for (column, case) in cases.unwrap().chars().enumerate() {
            let content = match case {
                '|' => Some(State::Pipe),
                '-' => Some(State::Minus),
                '/' => Some(State::BottomLeft),
                '\\' => Some(State::UpRight),
                _ => None,
            };
            if content.is_some() {
                map.push(Case {
                    x: column,
                    y: line,
                    state: content.unwrap(),
                });
            }
        }
    }
    run((0, 0, Direction::E), &mut map, x_max, y_max);
    /*dbg!(column(&map, 0, 0, 3));
    dbg!(column(&map, 1, 3, y_max));
    dbg!(line(&map, 3, 4, x_max));*/
    let not_counted = vec![State::BottomLeft, State::UpRight, State::Pipe, State::Minus];
    let a = map
        .iter()
        .filter(|case| !not_counted.contains(&case.state))
        .map(|case| case.x * 100 + case.y)
        .unique()
        .sorted()
        .collect_vec();
    dbg!(a);
    display_only_nrj(&map, x_max, y_max);
    //let mut win = HashSet::new();

    return 12;
}

fn part2() -> usize {
    return 12;
}

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() > 1 && args[1] == "part2" {
        println!("Part 2: {}", part2());
    } else {
        println!("Part 1: {}", part1());
    }
}
