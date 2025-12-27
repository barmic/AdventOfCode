import gleeunit
import day02_1

pub fn main() -> Nil {
  gleeunit.main()
}

pub fn foo_test() {
  let result = day02_1.compute(day02_1.Range(222220, 222224))

  assert result == [222222]
}

pub fn first_example_test() {
  let result = day02_1.compute(day02_1.Range(11, 22))

  assert result == [11, 22]
}