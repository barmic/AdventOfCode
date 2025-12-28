import gleam/list
import gleeunit
import day03_1

pub fn main() -> Nil {
  gleeunit.main()
}

pub fn bar_test() {
  let cases = [
    #("11119", 19),
    #("9981111", 99),
    #("1191819181111", 99),
    #("111111111", 11),
    #("119181911111", 99),
  ]

  cases
  |> list.each(fn (c) {
    let result = day03_1.compute(c.0)

    assert result == c.1
  })
}
