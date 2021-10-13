package exercises

/**
 * Lazy monad
 *
 * @param value
 * @tparam A
 */
class Lazy[+A](value: => A) {
  // call by need
  private lazy val intervalValue = value
  def flatMap[B](f: (=> A) => Lazy[B]): Lazy[B] = f(intervalValue) // f receives the parameter by name
  def use: A = intervalValue
}

object Lazy {
  def apply[A](value: => A): Lazy[A] = new Lazy[A](value) // unit
}

@main
def testLazy(): Unit = {
  val lazyInstance = Lazy {
    println("Today I don't fell like doing anything")
    42
  }

  val flatMappedInstance = lazyInstance.flatMap(x => Lazy {
    10 * x
  })

  val flatMappedInstance2 = lazyInstance.flatMap(x => Lazy {
    10 * x
  })

  flatMappedInstance.use
  flatMappedInstance2.use

  /*
  left identity
  unit.flatMap(f) = f(v)
  Lazy(v).flatMap(f) = f(v)

  right identity
  l.flatMap(unit) = l
  Lazy(v).flatMap(x => Lazy(x)) = Lazy(v)

  associativity
  l.flatMap(f).flatMap(g) = l.flatmap(x => f(x).flatMap(g))
  Lazy(v).flatMap(f).flatMap(g) = f(v).flatMap(g)
  Lazy(v).flatMap(x => f(x).flatMap(g)) = f(v).flatMap(g)
  */
}
