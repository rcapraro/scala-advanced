package lectures.part4implicits

object ExtensionMethods {

  case class Person(name: String) {
    def greet(): String = s"Hi, I'm $name, how can I help?"
  }

  extension (string: String) { // extension method
    def greetAsPerson(): String = Person(string).greet()
  }

  // extension methods <=> implicit classes
  object Scala2ExtensionMethods {
    implicit class RichInt(val self: Int) extends AnyVal {
      def isEven: Boolean = self % 2 == 0

      def sqrt: Double = Math.sqrt(self)

      def times(f: () => Unit): Unit = {
        def timesAux(n: Int): Unit =
          if (n <= 0) ()
          else {
            f()
            timesAux(n - 1)
          }

        timesAux(self)
      }
    }
  }

  extension (self: Int) {
    def isEven: Boolean = self % 2 == 0

    def sqrt: Double = Math.sqrt(self)

    def times(f: () => Unit): Unit = {
      def timesAux(n: Int): Unit =
        if (n <= 0) ()
        else {
          f()
          timesAux(n - 1)
        }

      timesAux(self)
    }
  }

  // generic extensions
  extension [A](list: List[A]) {
    def ends: (A, A) = (list.head, list.last)
    def extremes(using ordering: Ordering[A]): (A, A) = list.sorted.ends // can call extension methods here!
  }

  @main
  def testExtensionMethods() = {

    val richardsGreeting = "Richard".greetAsPerson()
    println(richardsGreeting)

    val is3even = 3.isEven
    println(isEven)

  }

}
