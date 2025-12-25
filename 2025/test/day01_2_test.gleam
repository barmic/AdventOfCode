import gleam/int
import gleam/list
import gleeunit
import day01_2

pub fn main() -> Nil {
  gleeunit.main()
}

fn compute(inputs: List(Int)) {
  inputs
  |> list.scan(day01_2.Flip(50, 0), day01_2.compute)
  |> list.fold(0, fn(a, b) {
      a + b.time
    })
}

pub fn grow_not_reach_100_test() {
  let size = int.random(10)
  let input = list.range(1, size)
    |> list.map(fn(_x) { int.random(10) })

  let a = list.fold(input, 0, fn(a, b) {a + b})

  assert a < 50
  let result = compute(input)

  assert result == 0
}

pub fn zero_to_zero_test() {
  let size = int.random(10)
  let input = list.range(1, size)
    |> list.flat_map(fn(_x) { [-100, 100] })
  let input = echo list.prepend(input, -50)

  let result = compute(input)

  assert result == {size * 2} + 1
}

pub fn site_example_test() {
  let result = compute([
    -68,
    -30,
    48,
    -5,
    60,
    -55,
    -1,
    -99,
    14,
    -82,
  ])

  assert result == 6
}

pub fn example_test() {
  let result = compute([
    -50,
    -100,
    100,
  ])

  assert result == 3
}