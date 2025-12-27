import gleam/float
import gleam/int
import argv
import gleam/list
import gleam/io
import gleam/result
import gleam/string
import simplifile
import gleam/yielder
import gleam_community/maths

fn read_file() -> List(String) {
  case argv.load().arguments {
    [path] ->
      simplifile.read(path)
      |> result.map(string.split(_, "\n"))
      |> result.unwrap([])
      |> list.flat_map(string.split(_, ","))
    _ -> []
  }
}

pub type Range {
  Range(from: Int, to: Int)
}

pub fn size(a: Int) -> Int {
  case a {
    1 -> 1
    _ -> {
      int.to_float(a)
      |> maths.logarithm_10
      |> result.unwrap(0.)
      |> float.ceiling
      |> float.truncate
      |> fn(x) {
        case a % pow(10, x) == 0 {
          True -> 1 + x
          False -> x
        }
      }
    }
  }
}

pub fn twice(a: Int) -> Int {
  int.to_float(size(a))
  |> int.power(10, _)
  |> result.unwrap(0.)
  |> float.truncate
  |> fn (x) { x * a + a }
}

fn pow(a: Int, p: Int) -> Int {
  float.truncate(int.power(a, int.to_float(p)) |> result.unwrap(0.))
}

pub fn compute(a: Range) -> List(Int) {
  let from_size = size(a.from)
  let to_size = size(a.to)
  case int.is_odd(from_size), from_size == to_size {
    True, True -> []
    _, _ -> {
      yielder.unfold(pow(10, from_size / 2 - 1), fn(n) {
        yielder.Next(element: n, accumulator: n + 1)
      })
      |> yielder.map(twice)
      |> yielder.filter(fn (x) { x >= a.from })
      |> yielder.take_while(fn (x) { x <= a.to })
      |> yielder.to_list
    }
  }
}

fn parse_range(range: String) -> List(Range) {
    let parts = string.split(range, "-")
    |> list.map(int.base_parse(_, 10))
    |> list.map(result.unwrap(_, 0))

    case parts {
        [start, end] -> [Range(start, end)]
        _ -> []
    }
}

fn part1(lines: List(String)) -> Nil {
  lines
  |> list.flat_map(parse_range)
  |> list.flat_map(compute)
  |> list.fold(0, fn(a, b) {
      a + b
    })
  |> int.to_string
  |> io.println
}

pub fn main() {
  case read_file() {
    [] -> io.println_error("Usage: programme <chemin_fichier>")
    lines -> part1(lines)
  }
}