package lectures.part5ts

object TypeMembers {

  class Animal

  class Dog extends Animal

  class Cat extends Animal

  class AnimalCollection {
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal // abstract type member
    type SuperBoundedAnimal >: Dog <: Animal // bounded type
    type AnimalCat = Cat // alias
  }

  @main
  def testTypeMembers(): Unit = {
    val ac = new AnimalCollection
    val dog: ac.AnimalType = ???

    // val cat: ac.BoundedAnimal = new Cat // not compile
    val pup: ac.SuperBoundedAnimal = new Dog
    val cat: ac.AnimalCat = new Cat

    type CatAlias = Cat
    val anotherCat: CatAlias = new Cat

    // type member can ver overriden - alternative to generics
    trait MyList {
      type T

      def add(element: T): MyList
    }

    class NonEmptyList(value: Int) extends MyList {
      override type T = Int

      override def add(element: Int): MyList = ???
    }

    // .type
    type CatsType = cat.type

    val newCat: CatsType = cat
    // new CatsType // is is constructable or not ???

    trait MList {
      type A

      def head: A

      def tail: MList
    }

    trait ApplicableToNumbers {
      type A <: Number
    }

    /*
    class CustomList(hd: String, tl: CustomList) extends MList with ApplicableToNumbers {
      type A = String
      def head = hd
      def tail = tl
    }
    */

    class IntList(hd: Integer, tl: IntList) extends MList with ApplicableToNumbers {
      type A = Integer

      def head = hd

      def tail = tl
    }

  }

}
