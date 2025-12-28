import gleeunit
import day02_2

pub fn main() -> Nil {
  gleeunit.main()
}

pub fn foo_test() {
  let result = day02_2.duplicate(12, 3)

  assert result == 121212
}

pub fn bar_test() {
  let result = day02_2.generate(day02_2.Range(95, 115), 1, 2)

  assert result == [99]
}
