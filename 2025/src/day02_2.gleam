import gleam/set
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

pub fn duplicate(a: Int, times: Int) -> Int {
  let size = int.to_float(size(a))
  |> int.power(10, _)
  |> result.unwrap(0.)
  |> float.truncate
  list.repeat(a, times)
  |> list.fold(0, fn(a, b) {
    a * size + b
  })
}

fn pow(a: Int, p: Int) -> Int {
  float.truncate(int.power(a, int.to_float(p)) |> result.unwrap(0.))
}

pub fn generate(a: Range, digits: Int, dups: Int) -> List(Int) {
  case dups <= 1 {
    True -> []
    False -> {
      //echo #("digits", digits, "dups", dups)
      yielder.unfold(pow(10, digits - 1), fn(n) {
        yielder.Next(element: n, accumulator: n + 1)
      })
      |> yielder.map(duplicate(_, dups))
      |> yielder.filter(fn (x) { x >= a.from })
      |> yielder.take_while(fn (x) { x <= a.to })
      |> yielder.to_list
    }
  }
}

pub fn compute(a: Range) -> List(Int) {
  let from_size = size(a.from)
  let to_size = size(a.to)
  list.range(1, to_size)
  |> list.filter(fn(x) { int.remainder(from_size, x) == Ok(0) || int.remainder(to_size, x) == Ok(0)  })
  |> list.flat_map(fn(x) { generate(a, from_size / x, x) })
  |> fn(x) {
    //echo string.inspect(a) <> " -> [" <> string.join(list.map(x, int.to_string), ":") <> "]"
    x
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
  |> set.from_list
  |> set.fold(0, fn(a, b) {
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