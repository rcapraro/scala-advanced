package lectures.part5ts

object FBoundedPolymorphism {

  @main
  def testFBoundedPolymorphism(): Unit = {

    /*
    trait Animal {
      def breed: List[Animal]
    }

    class Cat extends Animal {
      override def breed: List[Animal] = ??? // I want List[Cat] !
    }

    class Dog extends Animal {
      override def breed: List[Animal] = ??? // I want List[Dog] !
    }
    */

    // Solution 1 - naive but error prone
    /*
    trait Animal {
      def breed: List[Animal]
    }

    class Cat extends Animal {
      override def breed: List[Cat] = ??? // compile with List[Dog]...
    }

    class Dog extends Animal {
      override def breed: List[Dog] = ??? // compile with List[Cat]...
    }
    */

    // Solution 2 - FBP
    /*
    trait Animal[A <: Animal[A]] { // recursive type: F-Bounded Polymorphism
      def breed: List[Animal[A]]
    }

    class Cat extends Animal[Cat] {
      override def breed: List[Animal[Cat]] = ???
    }

    class Dog extends Animal[Dog] {
      override def breed: List[Animal[Dog]] = ???
    }

    // other examples
    trait Entity[E <: Entity[E]] //ORM
    class Person extends Comparable[Person] {
      override def compareTo(o: Person): Int = ???
    }

    // but i can made error
    class Crocodile extends Animal[Dog] {
      override def breed: List[Animal[Dog]] = ??? // List[Dog] !!
    }
    */

    // Solution 3 - FBP + self types
    /*
    trait Animal[A <: Animal[A]] { self: A =>  // every descendant must be an A
      def breed: List[Animal[A]]
    }

    class Cat extends Animal[Cat] {
      override def breed: List[Animal[Cat]] = ???
    }

    class Dog extends Animal[Dog] {
      override def breed: List[Animal[Dog]] = ???
    }

    // does not compile: cool!
    /*
    class Crocodile extends Animal[Dog] {
      override def breed: List[Animal[Dog]] = ???
    }
    */

    // limitations
    trait Fish extends Animal[Fish]
    class Shark extends Fish {
      override def breed: List[Animal[Fish]] = ??? // I want Animal[Shark]
    }
    */

    // Solution 4 - type classes!
    /*
    trait Animal

    trait CanBreed[A] {
      def breed(a: A): List[A]
    }

    class Dog extends Animal
    object Dog {
      implicit object DogsCanBreed extends CanBreed[Dog] {
        override def breed(a: Dog): List[Dog] = List()
      }
    }

    implicit class CanBreedOps[A](animal: A) {
      def breed(implicit canBreed: CanBreed[A]): List[A] = canBreed.breed(animal)
    }

    val dog = new Dog
    dog.breed // List[Dog]
    // new CanBreedOps[Dog].breed(Dog.DogsCanBreed)

    class Cat extends Animal
    object Cat {
      implicit object CatsCanBreed extends CanBreed[Dog] { // mistake!
        override def breed(a: Dog): List[Dog] = List()
      }
    }

    val cat = new Cat
    // cat.breed // try to find an implicit instance of CanBreed[Cat] !
    */

    // Solution 5
    trait Animal[A] { // pure type classes
      def breed(a: A): List[A]
    }

    class Dog
    object Dog {
      implicit object DogAnimal extends Animal[Dog] {
        override def breed(a: Dog): List[Dog] = List()
      }
    }

    implicit class AnimalOps[A](animal: A) {
      def breed(implicit animalTypeClassInstance: Animal[A]): List[A] = animalTypeClassInstance.breed(animal)
    }

    val dog = new Dog
    dog.breed

    class Cat
    object Cat {
      implicit object CatAnimal extends Animal[Dog] {
        override def breed(a: Dog): List[Dog] = List()
      }
    }

    val cat = new Cat
    // cat.breed // no implicit of type Animal[Cat] !

  }

}
