package lectures.part2afp

object LazyEvaluation {

  @main
  def testLazyEvaluation(): Unit = {
    //lazy delays the evaluation of values
    lazy val x: Int = throw new RuntimeException

    lazy val y: Int = {
      println("hello")
      42
    }

    println(y) //prints hello
    println(y) //does not print hello

    // exemples of implication
    // side effects
    def sideEffetCondition: Boolean = {
      println("Boo")
      true
    }

    def simpleCondition: Boolean = false

    lazy val lazyCondition = sideEffetCondition

    println(if simpleCondition && lazyCondition then "yes" else "no") //lazyCondition is not evaluated because simplecondition is false !

    // in conjunction with call by name
    def byName(n: => Int): Int = n + n + n + 1

    def byNameLazy(n: => Int): Int = {
      // CALL BY NEED TECHNIC
      lazy val t = n //only evaluated one
      t + t + t + 1
    }

    def retrieveMagicValue = {
      //side effect or long computation
      Thread.sleep(1000)
      42
    }

    // println(byName(retrieveMagicValue)) //waiting 1s + 1s + 1s !
    //use lazy val !
    println(byNameLazy(retrieveMagicValue)) //waiting 1s

    //filtering with lazy vals
    def lessThan30(i: Int): Boolean = {
      println(s"$i is less than 30?")
      i < 30
    }

    def greaterThan20(i: Int): Boolean = {
      println(s"$i is greater than 20?")
      i > 20
    }

    val numbers = List(1, 25, 40, 5, 23)
    val lt30 = numbers.filter(lessThan30) // List(1, 25, 5, 23)
    val gt20 = lt30.filter(greaterThan20) // List(25, 23)
    println(gt20)

    val lt30lazy = numbers.withFilter(lessThan30) //lazy values under the hood
    val gt20lazy = lt30lazy.withFilter(greaterThan20)
    println()
    gt20lazy.foreach(println) //evaluated one by one

    // for-comprehension use withFilter with guards
    for {
      a <- List(1, 2, 3) if a % 2 == 0 //use lazy vals
    } yield a + 1

    //this translates to:
    List(1, 2, 3).withFilter(_ % 2 == 0).map(_ + 1)

  }

}
