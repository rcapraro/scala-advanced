package lectures.part2afp

object PartialFunction {

  class FunctionNotApplicableException extends RuntimeException

  @main
  def testPartialFunctions(): Unit = {
    val aFunction = (x: Int) => x + 1 //Function1[Int, Int] or Int => Int

    val aFussyFunction = (x: Int) =>
      if x == 1 then 42
      else if x == 2 then 56
      else if x == 5 then 999
      else throw new FunctionNotApplicableException

    val aNicerFussyFunction = (x: Int) => x match {
      case 1 => 42
      case 2 => 56
      case 5 => 999
    }

    // Function from domain {1,2,5} to Int
    // This is partial function fromt Int to Int

    val aPartialFunction: PartialFunction[Int, Int] = {
      case 1 => 42
      case 2 => 56
      case 5 => 999
    } //partial function value

    println(aPartialFunction(2))
    // println(aPartialFunction(12345))

    // PF utilities
    println(aPartialFunction.isDefinedAt(67))

    // lift
    val lifted = aPartialFunction.lift //Int => Option[Int]
    println(lifted(2))
    println(lifted(12345))

    val pfChain = aPartialFunction.orElse[Int, Int] {
      case 12345 => 54321
    }

    println(pfChain(2))
    println(pfChain(12345))

    // PF extends normal functions
    val aTotalFunction: Int => Int = {
      case 1 => 99
    }

    // HOFs accept partial function as well
    val aMappedList = List(1, 2, 3).map {
      case 1 => 42
      case 2 => 78
      case 3 => 1000
    }
    println(aMappedList)

    // Note: A PF can only have one parameter type

    // PF instance (anonymous class)
    val myPF = new PartialFunction[Int, Int] {
      override def apply(x: Int): Int = x match {
        case 1 => 42
        case 2 => 65
        case 5 => 999
      }

      override def isDefinedAt(x: Int): Boolean = x == 1 || x == 2 | x == 3
    }

    // chatbot as a partial function
    val chatbot:  PartialFunction[String, String] = {
      case "hello" => "Hi,my name is HAL9000"
      case "goodbye" => "Once you start talkign to me, there is no return, human!"
      case "call mom" => "Unable to find you phone without your credit card"
    }
    scala.io.Source.stdin.getLines().map(chatbot).foreach(println)
  }

}
