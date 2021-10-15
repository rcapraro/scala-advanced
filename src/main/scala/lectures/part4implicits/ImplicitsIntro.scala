package lectures.part4implicits

object ImplicitsIntro {

  @main
  def testImplicitsIntro (): Unit = {
    val pair = "Richard" -> "555"
    val intPair = 1 -> 2

    case class Person(name: String) {
      def greet = s"Hi, my name is $name!"
    }

    implicit def fromStringToPerson(str: String): Person = Person(str)

    println("Peter".greet) // compiler -> println(fromStringToPerson("Peter").greet)

    // implciti parameters
    def increment(x: Int)(implicit amount: Int) = x + amount
    implicit val defaultAmount = 10
    println(increment(2))

  }

}
