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

fn part1(lines: List(String)) -> Nil {
  lines
  |> list.map(string.replace(_, each: "L", with: "-"))
  |> list.map(string.replace(_, each: "R", with: ""))
  |> list.map(int.base_parse(_, 10))
  |> list.map(result.unwrap(_, 0))
  |> list.scan(50, fn(a, b) {
      case {a + b} % 100 < 0 {
        True -> a + b + 100
        False -> {a + b} % 100
      }
    })
  |> list.filter(fn(x) { x == 0})
  |> list.length
  |> int.to_string
  |> io.println
}

pub fn main() {
  case read_file() {
    [] -> io.println_error("Usage: programme <chemin_fichier>")
    lines -> part1(lines)
  }
}