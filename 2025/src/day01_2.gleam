import gleam/int
import argv
import gleam/list
import gleam/io
import gleam/result
import gleam/string
import simplifile
import gleam/order

fn read_file() -> List(String) {
  case argv.load().arguments {
    [path] ->
      simplifile.read(path)
      |> result.map(string.split(_, "\n"))
      |> result.unwrap([])
    _ -> []
  }
}

pub type Flip {
  Flip(value: Int, time: Int)
}

pub fn compute(a: Flip, b: Int) -> Flip {
  let r = result.unwrap(int.remainder(b, 100), 0)
  let e = result.unwrap(int.modulo(a.value + r, 100), 0)
  // finish on 0
  let p1 = case a.value, int.compare(a.value + r, 0), int.compare(a.value + r, 100) {
    0, _, _ -> 0
    _, order.Gt, order.Lt -> 0
    _, _, _ -> 1
  }
  // number of full loop
  let p2 = int.absolute_value(b) / 100
  let p = p1 + p2
  case int.compare(p, 0), int.compare(a.value, 0) {
    order.Gt, order.Eq -> Flip(e, p)
    _, _ -> Flip(e, p)
  }
}

fn part2(lines: List(String)) -> Nil {
  lines
  |> list.map(string.replace(_, each: "L", with: "-"))
  |> list.map(string.replace(_, each: "R", with: ""))
  |> list.map(int.base_parse(_, 10))
  |> list.map(result.unwrap(_, 0))
  |> list.scan(Flip(50, 0), compute)
  |> list.fold(0, fn(a, b) {
      a + b.time
    })
  |> int.to_string
  |> io.println
}

pub fn main() {
  case read_file() {
    [] -> io.println_error("Usage: programme <chemin_fichier>")
    lines -> part2(lines)
  }
}