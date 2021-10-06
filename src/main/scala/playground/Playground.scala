package playground

import java.awt.Composite

object Playground {

  @main
  def main(): Unit = {

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

    //also works !
    val anAbstractType: AnAbstractType = a => println("sweet")

    //infix types
    class Composite[A, B]
    val composite: Composite[Int, String] = ???
    val composite2: Int Composite String = ???

    class -->[A, B]
    val towards: Int --> String = ???

    // update() is very special like apply()
    val anArray = Array(1,2,3)
    anArray(2) = 7 //rewritten to anArray.update(2, 7) - used in mutable collections

    // setters for mutable containers
    class Mutable {
      private var internalMember: Int = 0 // OO encapsulation
      def member = internalMember // "getter"
      def member_= (value: Int): Unit = internalMember = value
    }

    val aMutableContainer = new Mutable
    aMutableContainer.member = 42 //rewritten as aMutableContainer.member_=(42)
  }

}
