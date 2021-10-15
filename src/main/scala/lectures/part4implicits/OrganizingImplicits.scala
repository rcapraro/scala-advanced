package lectures.part4implicits

import javax.management.PersistentMBean

object OrganizingImplicits {

  @main
  def testOrganizingImplicits(): Unit = {

    implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
    //implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_ < _)

    println(List(1, 4, 5, 3, 2).sorted) //scala.Predef by default

    /*
    Implicits (used as implicit parameters)
      - val / var
      - object
      - accessor methods - defs with no parenthesis
    */

    // implicit val personOrderingbyAge: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)
    // implicit val personOrderingbyName: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
    case class Person(name: String, age: Int)

    val persons = List(
      Person("Steve", 30),
      Person("Amy", 22),
      Person("John", 66)
    )
    // println(persons.sorted)

    /*
    Implicit scope by priority
      - normal scope = LOCAL scope
      - imported scope
      - companion object of all types involved in the method signature. ex for List[A]
        - List
        - Ordering
        - all the types involved = A or any supertype
    */

    object AlphabeticNameOrdering {
      implicit val personOrderingbyName: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
    }

    object AgeOrdering {
      implicit val personOrderingbyAge: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)
    }

    import AlphabeticNameOrdering.*
    println(persons.sorted)

    case class Purchase(nUnits: Int, unitPrice: Double) {
      def totalPrice: Double = nUnits * unitPrice
    }

    object Purchase {
      // most often used ordering
      implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => a.totalPrice < b.totalPrice)
    }

    object UnitCountOrdering {
      implicit val unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => a.nUnits < b.nUnits)
    }

    object UnitPriceOrdering {
      implicit val unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => a.unitPrice < b.unitPrice)
    }

  }

}
