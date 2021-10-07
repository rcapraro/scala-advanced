package lectures.part1as

import scala.util.Try

object DarkSugars {

  @main
  def testDarkSugars(): Unit = {

    def singleArgsMethod(arg: Int): String = s"$arg little ducks..."

    val description = singleArgsMethod {
      42
    }

    val aTryInstance = Try {
      throw new RuntimeException
    }

    val list = List(1, 2, 3)
    println(list.map {
      x => x * 2
    })

    trait Action {
      def run(n: Int): Int
    }

    def multiplierBy10: Action = x => x * 10

    println(multiplierBy10.run(3))

    val aThread = new Thread(new Runnable {
      override def run(): Unit = println("hello, scala!")
    })
    val aSweetThread = new Thread(() => println("sweet, scala!"))

    abstract class AnAbstractType {
      def implemented: Int = 42

      def f(a: Int): Unit
    }

    val anAbstractType: AnAbstractType = a => println("sweet")

  }

}
