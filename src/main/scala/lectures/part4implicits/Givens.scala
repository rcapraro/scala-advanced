package lectures.part4implicits

object Givens {

  @main
  def testGivens(): Unit = {
    val aList = List(1, 2, 3, 4)
    // val anOrderedList = aList.sorted // implicit Ordering[Int]

    // Scala 2 style (need to import Implicits.* to use it)
    object Implicits {
      implicit val descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
    }

    // Scala 3 style
    object Givens {
      given descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
    } // givens <=> implicit vals

    // instantiating an anonymous class
    object GivenAnonymousClassNaive {
      given descendingOrdering_v2: Ordering[Int] = new Ordering[Int] {
        override def compare(x: Int, y: Int): Int = y - x
      }
    }

    // better
    object GivenWith {
      given descendingOrdering_v3: Ordering[Int] with {
        override def compare(x: Int, y: Int): Int = y - x
      }
    }

    // import GivenWith._ // in scala 3, this import does NOT import givens as well
    // this imports all given

    // implicit arguments <=> using clauses
    def extremes[A](list: List[A])(implicit ordering: Ordering[A]): (A, A) = {
      val sortedList = list.sorted
      (sortedList.head, sortedList.last)
    }

    def extremes_v2[A](list: List[A])(using ordering: Ordering[A]): (A, A) = {
      val sortedList = list.sorted
      (sortedList.head, sortedList.last)
    }

    // implicit def (synthesize new implicit values)
    trait Combinator[A] { // semigroup
      def combine(x: A, y: A): A
    }

    // implement list comparison based on sums List(1,2,3) < List(4,5,6)
    implicit def listOrdering[A](implicit simpleOrdering: Ordering[A], combinator: Combinator[A]): Ordering[List[A]] =
      new Ordering[List[A]] {
        override def compare(x: List[A], y: List[A]): Int =
          val sumX = x.reduce(combinator.combine)
          val sumY = y.reduce(combinator.combine)
          simpleOrdering.compare(sumX, sumY) // compare the sums
      }

    // equivalent in Scala 3 with givens
    given listOrdering_v2[A] (using simpleOrdering: Ordering[A], combinator: Combinator[A]): Ordering[List[A]] with {
      override def compare(x: List[A], y: List[A]): Int =
        val sumX = x.reduce(combinator.combine)
        val sumY = y.reduce(combinator.combine)
        simpleOrdering.compare(sumX, sumY) // compare the sums
    }

    // implicit conversions (abused in Scala 2)
    case class Person(name: String) {
      def greet(): String = s"Hi, my name is $name"
    }

    // danger !
    // implicit def string2Person(string: String): Person = Person(string)
    // val richardsGreet = "Richard".greet()

    // Scala 3
    import scala.language.implicitConversions // required
    given string2PersonConversion: Conversion[String, Person] with {
      override def apply(x: String): Person = Person(x)
    }

    val anOrderedList = aList.sorted
    println(anOrderedList)

    println(extremes(aList))

    println("Richard".greet())

  }
}
