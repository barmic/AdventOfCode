use std::env;
use std::io::{self, BufRead};
use std::collections::HashSet;

fn north((_, x, y): (char, i32, i32)) -> (char, i32, i32) {
    return ('N', x, y - 1);
}
fn south((_, x, y): (char, i32, i32)) -> (char, i32, i32) {
    return ('S', x, y + 1);
}
fn east((_, x, y): (char, i32, i32)) -> (char, i32, i32) {
    return ('E', x + 1, y);
}
fn west((_, x, y): (char, i32, i32)) -> (char, i32, i32) {
    return ('W', x - 1, y);
}

fn get(map: &Vec<String>, (_, x, y): (char, i32, i32)) -> Option<char> {
    let x_size = usize::try_from(x).ok();
    let y_size = usize::try_from(y).ok();
    if x_size == None || y_size == None {
        return None;
    }
    return map.get(y_size.unwrap())
        .map(|line| line.chars().nth(x_size.unwrap())).flatten();
}

fn get_full(map: &Vec<String>, (_, x, y): (char, i32, i32)) -> (char, i32, i32) {
    let x_size = usize::try_from(x).ok();
    let y_size = usize::try_from(y).ok();
    if x_size == None || y_size == None {
        return OOB;
    }
    return map.get(y_size.unwrap())
        .map(|line| line.chars().nth(x_size.unwrap())).flatten()
        .map(|c| (c, x, y))
        .unwrap_or(OOB);
}

static OOB: (char, i32, i32) = ('.', -1, -1);

fn is_oob((_, x, y): (char, i32, i32)) -> bool {
    return x < 0 || y < 0;
}

fn part1() -> i64 {
    let mut map: Vec<String> = Vec::new();

    for (_, line) in io::stdin().lock().lines().enumerate() {
        map.push(line.unwrap());
    }


    let mut start = OOB;
    let mut i: usize = 0;
    while start == OOB && i < map.len() {
        let search = map.get(i).unwrap().find('S');
        if search != None {
            start = ('.', search.unwrap() as i32, i as i32);
        }
        i += 1;
    }

    let mut paths: Vec<(char, i32, i32)> = vec![north(start), south(start), east(start), west(start)];

    let mut steps = 1;

    while is_not_finish(&paths) {
        let mut new_paths: Vec<(char, i32, i32)> = Vec::new();
        for path in paths.iter() {
            let new_path = match (path.0, get(&map, *path)) {
                ('N', Some('|')) => Some(north(*path)),
                ('S', Some('|')) => Some(south(*path)),

                ('E', Some('-')) => Some(east(*path)),
                ('W', Some('-')) => Some(west(*path)),

                ('S', Some('L')) => Some(east(*path)),
                ('W', Some('L')) => Some(north(*path)),

                ('S', Some('J')) => Some(west(*path)),
                ('E', Some('J')) => Some(north(*path)),

                ('E', Some('7')) => Some(south(*path)),
                ('N', Some('7')) => Some(west(*path)),

                ('N', Some('F')) => Some(east(*path)),
                ('W', Some('F')) => Some(south(*path)),
                _ => None,
            };
            if new_path != None {
                new_paths.push(new_path.unwrap());
            }
        }
        paths = new_paths;
        steps += 1;
    }
    
    return steps;
}

fn is_not_finish(paths: &Vec<(char, i32, i32)>) -> bool {
    let mut win: HashSet<(&i32, &i32)> = HashSet::new();
    let mut not_finish = true;
    for (_, x, y) in paths {
        if !is_oob(('.', *x, *y)) {
            not_finish &= win.insert((x, y));
        }
    }
    return not_finish;
}

fn is_not_finish_p(paths: &Vec<Vec<(char, i32, i32)>>) -> bool {
    let mut win: HashSet<(&i32, &i32)> = HashSet::new();
    let mut not_finish = true;
    for p in paths {
        let (_, x, y) = p.last().unwrap();
        if !is_oob(('.', *x, *y)) {
            not_finish &= win.insert((x, y));
        }
    }
    return not_finish;
}

fn part2() -> i64 {
    let mut map: Vec<String> = Vec::new();

    for (_, line) in io::stdin().lock().lines().enumerate() {
        map.push(line.unwrap());
    }


    let mut start = OOB;
    let mut i: usize = 0;
    while start == OOB && i < map.len() {
        let search = map.get(i).unwrap().find('S');
        if search != None {
            start = ('.', search.unwrap() as i32, i as i32);
        }
        i += 1;
    }

    let mut paths: Vec<Vec<(char, i32, i32)>> = vec![
        vec![north(start)],
        vec![south(start)],
        vec![east(start)],
        vec![west(start)],
    ];

    while is_not_finish_p(&paths) {
        let mut new_paths: Vec<Vec<(char, i32, i32)>> = Vec::new();
        for path in paths.iter() {
            let last_step = path.last().unwrap();
            let new_step = match (last_step.0, get(&map, *last_step)) {
                ('N', Some('|')) => Some(north(*last_step)),
                ('S', Some('|')) => Some(south(*last_step)),

                ('E', Some('-')) => Some(east(*last_step)),
                ('W', Some('-')) => Some(west(*last_step)),

                ('S', Some('L')) => Some(east(*last_step)),
                ('W', Some('L')) => Some(north(*last_step)),

                ('S', Some('J')) => Some(west(*last_step)),
                ('E', Some('J')) => Some(north(*last_step)),

                ('E', Some('7')) => Some(south(*last_step)),
                ('N', Some('7')) => Some(west(*last_step)),

                ('N', Some('F')) => Some(east(*last_step)),
                ('W', Some('F')) => Some(south(*last_step)),
                _ => None,
            };
            if new_step != None {
                let mut nnpath: Vec<(char, i32, i32)> = Vec::new();
                for ele in path {
                    nnpath.push(*ele);
                }
                nnpath.push(new_step.unwrap());
                new_paths.push(nnpath);
            }
        }
        paths = new_paths;
    }

    let mut limits: HashSet<(i32, i32)> = HashSet::new();
    for p in paths {
        for s in p {
            limits.insert((s.1, s.2));
        }
    }

    let mut win: HashSet<(i32, i32)> = HashSet::new();

    let mut result = 0;
    // columns
    for i in /*vec![14usize]*/0..map.first().unwrap().len() {
        let mut dot = get_full(&map, ('.', i as i32, 0));
        let mut inside = 0;
        while dot != OOB {
            if limits.contains(&(dot.1, dot.2)) && dot.0 != '|' && dot.0 != '-' {
                inside += 1;
                //dbg!(inside, dot);
            } else if inside % 2 == 0 && !win.insert((dot.1, dot.2)) {
                result += 1;
            }
            dot = get_full(&map, south(dot));
        }
    }
    dbg!(&win);

    // lines
    for i in /*vec![3usize]*/0..map.len() {
        let mut dot = get_full(&map, ('.', 0, i as i32));
        let mut inside = 0;
        while dot != OOB {
            if limits.contains(&(dot.1, dot.2)) && dot.0 != '|' && dot.0 != '-' {
                inside += 1;
                dbg!(inside, dot);
            } else if inside % 2 == 0 && !win.insert((dot.1, dot.2)) {
                dbg!(&dot);
                result += 1;
            }
            dot = get_full(&map, east(dot));
        }
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
