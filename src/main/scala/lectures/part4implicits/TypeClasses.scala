package lectures.part4implicits

object TypeClasses {

  @main
  def testTypeClasses(): Unit = {

    trait HTMLWritable {
      def toHTML: String
    }

    case class User(name: String, age: Int, email: String) extends HTMLWritable {
      override def toHTML: String = s"<div>$name ($age years old( <a href='$email'></div>'"
    }

    val john = User("John", 32, "john@rockthejvm.com")
    john.toHTML

    /*
      Disadvantages
      1 - works only for the type WE write
      2 - Only ONE implementation out of quite a number
    */

    // option 2 - pattern matching
    object HTMLSerializerPM {
      def serializeToHTML(value: Any) = value match {
        case User(name, age, email) => s"<div>$name ($age years old( <a href='$email'></div>'"
        case Double =>
        case _ =>
      }
    }

    /*
    Disadvantages
    1 - lost type safety
    2 - need to modify the code every time
    3 - still ONE implementation
    */

    // option 3

    trait HTMLSerializer[T] {
      def serialize(value: T): String
    }

    object UserSerializer extends HTMLSerializer[User] {
      override def serialize(user: User): String = s"<div>${user.name} ($user.age} years old( <a href='${user.email}'></div>'"
    }

    println(UserSerializer.serialize(john))

    // 1 - we can define serializers for other types
    import java.util.Date
    object DateSerializer extends HTMLSerializer[Date] {
      override def serialize(date: Date): String = s"<div>${date.toString}</div>'"
    }

    // 2 - we can define MULTIPLE serializers
    object PartialUserSerializer extends HTMLSerializer[User] {
      override def serialize(user: User): String =  s"<div>${user.name}</div>'"
    }

    // TYPE CLASS : specifies set of operation which can be applied to a given type (the type parameter)
    trait MyTypeClassTemplate[T] {
      def action(value: T): String // or some other type
    }

    object MyTypeClassTemplate {
      def apply[T](implicit instance: MyTypeClassTemplate[T]) = instance
    }

    /*
      Equality type class
    */
    trait Equal[T] {
      def apply(a: T, b: T): Boolean
    }

    object NameEquality extends Equal[User] {
      override def apply(a: User, b: User): Boolean = a.name == b.name
    }

    object FullEquality extends Equal[User] {
      override def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
    }

    // more power with implicit parameter
    object HTMLSerializer {
      def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)

      def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
    }

    implicit object IntSerializer extends HTMLSerializer[Int] {
      override def serialize(value: Int): String = s"<h1>$value</h1>"
    }

    println(HTMLSerializer.serialize(42))

    implicit object UserSerializerImplicit extends HTMLSerializer[User] {
      override def serialize(user: User): String = s"<div>${user.name} ($user.age} years old( <a href='${user.email}'></div>'"
    }

    println(HTMLSerializer.serialize(john))

    //access to the entire type class interface
    println(HTMLSerializer[User].serialize(john))

    object Equal {
      def apply[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean = equalizer.apply(a, b)
    }

    implicit object NameEqualityImplicit extends Equal[User] {
      override def apply(a: User, b: User): Boolean = a.name == b.name
    }

    val anotherJohn = User("John", 45, "anotherjohn@rtjvm.com")
    println(Equal(john, anotherJohn)) // true

    // AD-HOC polymorphism

  }

}
