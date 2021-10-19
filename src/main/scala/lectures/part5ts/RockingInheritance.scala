package lectures.part5ts

object RockingInheritance {

  @main
  def testRockingInheritance(): Unit = {

    // convenience
    trait Writer[T] {
      def write(value: T): Unit
    }

    trait Closeable {
      def close(status: Int): Unit
    }

    trait GenericStream[T] {
      // some methods
      def foreach(f: T => Unit): Unit
    }

    def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable): Unit = {
      stream.foreach(println)
      stream.close(0)
    }

    // diamond problem
    trait Animal {
      def name: String
    }

    trait Lion extends Animal {
      override def name: String = "LION"
    }

    trait Tiger extends Animal {
      override def name: String = "TIGER"
    }

    trait MutantTrait extends Lion with Tiger // OK

    class MutantO extends Lion with Tiger {
      override def name: String = "ALIEN" // OK if overriden
    }

    class Mutant extends Lion with Tiger {
    }

    val m = new Mutant
    println(m.name) //TIGER ?!

    /*
    Diamond problem !

    Mutant
    extends Animal with { override def name: String = "LION" }
    with { override def name: String = "TIGER" }

    LAST OVERRIDE GETS PICKED !
    */

    // the super problem + type linearization

    trait Cold {
      def print = println("COLD")
    }

    trait Green extends Cold {
      override def print: Unit = {
        println("GREEN")
        super.print
      }
    }

    trait Blue extends Cold {
      override def print: Unit = {
        println("BLUE")
        super.print
      }
    }

    class Red {
      def print = println("RED")
    }

    class White extends Red with Green with Blue {
      override def print: Unit = {
        println("WHITE")
        super.print
      }
    }

    val color = new White
    println("---")
    color.print
  }
}
