package exercises

object EqualityPlayground {

  case class User(name: String, age: Int, email: String)

  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  implicit object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }

  implicit object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean = equalizer.apply(a, b)
  }

  implicit object IntEqualizer extends Equal[Int] {
    override def apply(a: Int, b: Int): Boolean = a == b
  }

  // implicit conversion class
  implicit class TypeSafeEqual[T](value: T) {
    def ===(anotherValue: T)(implicit equalizer: Equal[T]): Boolean = equalizer(value, anotherValue)
    def !==(anotherValue: T)(implicit equalizer: Equal[T]): Boolean = !equalizer(value, anotherValue)
  }

  @main
  def testEqualityExercises(): Unit = {
    val john = User("John", 32, "john@rockthejvm.com")
    val anotherJohn = User("John", 45, "anotherjohn@rtjvm.com")
    println(Equal(john, anotherJohn))

    /*
    Exercise: improve the Equal TC with implicit conversion class
      ===(anotherValue: T)
      !==(anotherValue: T)
    */

    println(john === anotherJohn)

    println(1 !== 2)

  }

}
