import gleam/int
import argv
import gleam/list
import gleam/io
import gleam/result
import gleam/string
import simplifile

fn read_file() -> List(String) {
  case argv.load().arguments {
    [path] ->
      simplifile.read(path)
      |> result.map(string.split(_, "\n"))
      |> result.unwrap([])
    _ -> []
  }
}

fn max(line: List(Int), start: Int, stop: Int) -> #(Int, Int) {
  list.index_fold(line, #(-1, -1), fn(a, value, idx) {
    case idx >= start, idx < stop, value > a.0 {
      True, True, True -> #(value, idx)
      _, _, _ -> a
    }
  })
}

pub fn compute(a: String) -> Int {
  let values = string.split(a, "")
  |> list.map(int.parse)
  |> list.map(result.unwrap(_, -1))
  let high = max(values, 0, list.length(values) - 1)
  let low = max(values, high.1 + 1, list.length(values))
  echo #(a, high.0 * 10 + low.0, high, low)
  high.0 * 10 + low.0
}

fn part1(lines: List(String)) -> Nil {
  lines
  |> list.map(compute)
  //|> echo
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