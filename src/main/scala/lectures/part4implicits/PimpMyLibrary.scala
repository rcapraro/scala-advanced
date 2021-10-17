package lectures.part4implicits

object PimpMyLibrary {

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

    def *[T](list: List[T]): List[T] = {
      def concatenate(n: Int): List[T] = {
        if (n <= 0) List()
        else concatenate(n - 1) ++ list
      }

      concatenate(self)
    }
  }

  implicit class RichString(val self: String) extends AnyVal {
    def asInt: Int = Integer.valueOf(self)

    def encrypt(cypherDistance: Int): String = self.map(c => (c + cypherDistance).asInstanceOf[Char])
  }

  @main
  def testPimpMyLibrary(): Unit = {

    println(new RichInt(42).sqrt) // classical

    42.isEven //much better => type enrichment or pimping

    1 to 10 // implicit from RichInt from scala package

    println("3".asInt + "4".asInt)

    println("Richard".encrypt(2))

    3.times(() => println("Scala Rocks!"))
    println(4 * List(1, 2))

    // "3"/ 4
    implicit def stringToInt(str: String): Int = Integer.valueOf(str)

    println("6" / 2)

    //equivalent of an implicit class
    class RichAltInt(value: Int)

    implicit def enrich(value: Int): RichAltInt = new RichAltInt(value)

    // danger zone
    implicit def intToBoolean(i: Int): Boolean = i == 1  //avoid implicit def !

    /*
    if (n) do something else
    */
    val aConditionValue = if (3) "OK" else "Something wrong"
    println(aConditionValue)

  }

}
