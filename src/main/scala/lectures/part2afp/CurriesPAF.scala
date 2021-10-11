package lectures.part2afp

object CurriesPAF {

  @main
  def testCurriesPAF(): Unit = {

    //curried functions
    val superAdder: Int => Int => Int =
      x => y => x + y

    val add3 = superAdder(3) //Int => Int = y => 3 + y
    println(add3(5))

    println(superAdder(3)(5)) //curried functions

    // Method, not function !
    def curriedAdder(x: Int)(y: Int): Int = x + y //curried method

    val add4m = curriedAdder(4)

    val add4f: Int => Int = curriedAdder(4) //lifting: ETA-EXPANSION

    println(add4m(5))
    println(add4f(5))

    // functions != methods (JVM limitation)
    def inc(x: Int) = x + 1 //method

    println(List(1, 2, 3).map(inc)) //ETA-EXPANSION to function x => inc(x)

    // Partial function appications
    val add5 = curriedAdder(5) _ //do an ETA-EXPANSION for me !  Int => Int

    val simpleAddFunction = (x: Int, y: Int) => x + y

    def simpleAddMethod(x: Int, y: Int) = x + y

    def curriedAddMethod(x: Int)(y: Int) = x + y

    // add7: Int => Int = y => 7 + y
    val add7_1 = curriedAddMethod(7)
    val add7_2 = simpleAddFunction.curried(7)
    val add7_3 = (x: Int) => simpleAddFunction(7, x)
    val add7_4 = simpleAddMethod(7, _: Int) //ETA : y => simpleAddMethod(7, y)
    //etc

    // underscores are powerful
    def concatenator(a: String, b: String, c: String) = a + b + c

    val insertName = concatenator("Hello, I'm ", _, ", how are you?") // x: String => concatenator(hello, x, howareyou)
    println(insertName("Richard"))

    val fillInTheBlancks = concatenator("Hello, ", _, _) // (x, y) => concatenator(hello, x ,y)
    println(fillInTheBlancks("Richard, ", "Scala is awesome"))

    // curried formatter
    def curriedFormatter(s: String)(number: Double): String = s.format(number)

    val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)
    val simpledFormat = curriedFormatter("%4.2f") _ //lift, not necessary in scala 3
    val seriousFormat = curriedFormatter("%8.6f") _
    val preciseFormat = curriedFormatter("%14.12f") _

    println(numbers.map(simpledFormat))
    println(numbers.map(preciseFormat))

    def byName(n: => Int) = n + 1

    def byFunction(f: () => Int) = f() + 1

    def method: Int = 42

    def parenMethod(): Int = 42

    byName(23) // fine
    byName(method) // fine
    byName(parenMethod()) // fine
    // byName(parenMethod) // not fine
    // byName(() => 42) // not fine
    byName((() => 42)()) // fine
    // byName(parenMethod _) // not fine

    // byFunction(45) // not fine
    // byFunction(method) // not fine!!! no ETA
    byFunction(parenMethod) // fine - the compiler does ETA
    byFunction(parenMethod _) // fine but unnecessary
    byFunction(() => 46)
  }

}
