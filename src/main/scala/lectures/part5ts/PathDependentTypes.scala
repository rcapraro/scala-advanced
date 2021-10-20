package lectures.part5ts

object PathDependentTypes {

  class Outer {
    class Inner

    object InnerType

    type InnerType

    def print(i: Inner) = println(i)

    def printGeneral(i: Outer#Inner) = println(i)
  }

  def aMethod: Int = {
    class HelperClass
    type HelperType = String
    2
  }

  // DB
  class ItemLike {
    type Key
  }
  trait Item[K] extends ItemLike {
    type Key = K
  }
  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

   // def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???  //  TODO fix compilation

  @main
  def testPathDependantTypes(): Unit = {

    // per-instance
    val o = new Outer
    val inner = new o.Inner // o.Inner is a TYPE

    val oo = new Outer
    // val otherInner: oo.Inner = new o.Inner - differents types!

    o.print(inner)
    // oo.print(inner) - differents types!

    // path dependent types

    // Outer#Inner is a common type
    o.printGeneral(inner)
    oo.printGeneral(inner)

    // get[IntItem](42) // ok
    // get[StringItem]("home") // ok
    // get[IntItem]("scala") // not ok!
  }

}
