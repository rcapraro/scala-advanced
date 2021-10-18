package lectures.part4implicits

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MagnetPattern {

  @main
  def testMagnetPattern(): Unit = {
    //method overloading
    class P2PRequest
    class P2PResponse
    class Serializer[T]
    trait Actor {
      def receive(statusCode: Int): Int

      def receive(request: P2PRequest): Int

      def receive(response: P2PResponse): Int

      def receive[T: Serializer](message: T): Int

      def receive[T: Serializer](message: T, statusCode: Int): Int

      def receive(future: Future[P2PRequest]): Int

      // def receive(future: Future[P2PResponse]): Int // does not compile due to type erasure
      // lots of overloads
    }

    /*
    Problems:
      1 - type erasure
      2 - lifting does not work for all overloads
        val receiveFV = receive _ // ?!
      3 - code duplication
      4 - type inference and default args
        actor.receive(?!)
    */


    trait MessageMagnet[Result] {
      def apply(): Result
    }

    def receive[R](magnet: MessageMagnet[R]): R = magnet()

    implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
      override def apply(): Int = {
        // logic for handling a P2PRequest
        println("Handling P2P Request")
        42
      }
    }

    implicit class FromP2PResponse(request: P2PResponse) extends MessageMagnet[Int] {
      override def apply(): Int = {
        // logic for handling a P2PResponse
        println("Handling P2P Response")
        24
      }
    }

    receive(new P2PRequest) // implicit conversion happens
    receive(new P2PResponse)

    // Benefits:
    // 1 - no more type erasure problems
    implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int] {
      override def apply(): Int = 2
    }
    implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
      override def apply(): Int = 3
    }

    println(receive(Future(new P2PRequest)))

    println(receive(Future(new P2PResponse)))

    // 2 - lifting works
    trait MathLib {
      def add1(x: Int): Int = x + 1

      def add1(s: String): Int = s.toInt + 1
      // add 1 overloads
    }

    // "magnetize"
    trait AddMagnet {
      def apply(): Int
    }

    def add1(magnet: AddMagnet): Int = magnet()

    implicit class AddInt(x: Int) extends AddMagnet {
      override def apply(): Int = x + 1
    }

    implicit class AddString(s: String) extends AddMagnet {
      override def apply(): Int = s.toInt + 1
    }

    // val addFV = add1 _
    val addFV = add1 //thx scala 3!
    println(addFV(1))
    println(addFV("3"))

    val receiveFV = receive // MessageMagnet[Nothing] => Nothing :-(

    /*
    Drawbacks:
      1 - very verbose
      2 - harder to read
      3 - you can't name or place default argument
      4 - call by name doesn't work correctly
    */

    class Handler {
      def handle(s: => String) = {
        println(s)
        println(s)
      }
      // other overloads
    }

    trait HandlerMagnet {
      def apply(): Unit
    }

    def handle(magnet: HandlerMagnet) = magnet()

    implicit class StringHandler(s: => String) extends HandlerMagnet {
      override def apply(): Unit = {
        println(s)
        println(s)
      }
    }

    def sideEffectMethod(): String = {
      println("Hello, Scala")
      "ha ha ha"
    }

    handle(sideEffectMethod()) // works
    println()
    handle {
      println("Hello, Scala")
      "magnet" // only this value is converted to Magnet !
    }
  }
}