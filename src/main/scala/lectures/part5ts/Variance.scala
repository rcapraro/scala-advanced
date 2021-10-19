package lectures.part5ts

object Variance {

  @main
  def testVariance(): Unit = {
    trait Animal
    class Dog extends Animal
    class Cat extends Animal
    class Crocodile extends Animal

    // what is variance
    // "inheritance" - type substitution in generics

    class Cage[T]

    // Should a Cage[Cat] inherit a Cage[Animal] ?

    // yes: covariance
    class CCage[+T]
    val ccage: CCage[Animal] = new CCage[Cat]

    // no: invariance
    class ICage[T]
    // val icage: ICage[Animal] = new ICage[Cat] // wrong

    // hell no, opposite : contravariance
    class XCage[-T]
    val xcage: XCage[Cat] = new XCage[Animal] //makes sense !

    class InvariantCage[T](val animal: T) // invariant

    // covariant positions
    class Covariantcage[+T](val animal: T) // COVARIANT POSITION - complier accepts

    // class ContravariantCage[-T](val animal: T) - does not compile - COVARIANT POSITION
    /*
      it would be possible to write:
      val catCage: ContravariantCage[Cat] = new ContravariantCage[Animal](new Crocodile)
    */

    // class CovariantVariableCage[+T](var animal: T) //  does not compile - types of vars are in CONTRAVARIANT POSITION
    /*
      it would be possible to write:
      val catCage: CovariantVariableCage[Animal] = new CovariantVariableCage[Cat](new Cat)
      catCage.animal = new Crocodile
    */

    // class ContravariantVariableCage[-T](var animal: T) //  does not compile - types of vars are also on COVARIANT POSITION
    /*
      it would be possible to write:
      val catCage: ContravariantVariableCage[Cat] = new ContravariantVariableCage[Animal](new Crocodile)
    */


    // vars are both on COVARIANT and CONTRAVARIANT positions - the only possibility is INVARIANT
    class InvariantVariableCage[T](var animal: T) // ok

    trait AnotherCovariantCage[+T] {
      // def addAnimal(animal: T) // Method arguments are in CONTRAVARIANT POSITIONS !
      /*
        it would be possible to write:
        val dogCage: AnotherCovariantCage[Animal] = new AnotherCovariantCage[Dog]
        dogCage.add(new Cat)
        We don't want Cats and Dogs in the same Cage !
      */
    }

    class AnotherContravariantCage[-T] {
      def addAnimal(animal: T) = true
    }

    val cage: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
    cage.addAnimal(new Cat)
    // cage.addAnimal(new Dog) - no !

    class Kitty extends Cat
    cage.addAnimal(new Kitty)

    // how to solve the problem ?
    class MyList[+A] {
      def add[B >:A] (element: B): MyList[B] = new MyList[B] // widening the type
    }

    val emptyList = new MyList[Kitty]
    val animals = emptyList.add(new Kitty)
    val moreAnimal = animals.add(new Cat) // MyList[Cat]
    val evenMoreAnimals = moreAnimal.add(new Dog) // MyList[Animal] - common type !

    // METHOD ARGUMENTS are in CONTRAVARIANT POSITION.

    // return types
    class PetShop[-T] {
      // def get(isItAPuppy: Boolean): T // METHOD RETURN TYPE ARE IN COVARIANT POSITION
      /*
        it would be possible to write:
        val catShop: PetShop[Cat] = new PetShop[Animal] {
          def get(isItAPuppy: Boolean): Animal = new Cat
        }
        val dogShop: PetShop[Dog] = catShop
        dogShop.get(true) // gives me an evil cat!
      */

    // how to solve the problem ?
    def get[S <:T] (isItAPuppy: Boolean, defaultAnimal: S): S = defaultAnimal
    }

    val shop: PetShop[Dog] = new PetShop[Animal]
    // val eviCat = shop.get(true, new Cat) // no evil Cat!

    class TerraNova extends Dog
    val bigFurry = shop.get(true, new TerraNova)

    /*
      Big rule:
        - method arguments are in CONTRAVARIANT position
        - return types are in COVARIANT position
    */

  }

}
