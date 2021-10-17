package lectures.part4implicits

object TypeClasses {

  @main
  def testTypeClasses(): Unit = {

    trait HTMLWritable {
      def writeToHtml: String
    }

    case class User(name: String, age: Int, email: String) extends HTMLWritable {
      override def writeToHtml: String = s"<div>$name ($age years old) <a href='$email'></div>'"
    }

    val john = User("John", 32, "john@rockthejvm.com")
    john.writeToHtml

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

    implicit object UserSerializer extends HTMLSerializer[User] {
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

    // more power with implicit parameter
    object HTMLSerializer {
      def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)

      def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
    }

    implicit object IntSerializer extends HTMLSerializer[Int] {
      override def serialize(value: Int): String = s"<h1>$value</h1>"
    }

    println(HTMLSerializer.serialize(42))

    println(HTMLSerializer.serialize(john))

    //access to the entire type class interface
    println(HTMLSerializer[User].serialize(john))  // AD-HOC polymorphism

    // part 3
    implicit class HTMLEnrichment[T](value: T) {
      def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
    }

    println(john.toHTML) //UserSerializer is implicit also !

    /*
      we can:
        - extend the functionality to new types
        - choose implementation for the same type
        - super expressive
     */

    println(2.toHTML)

    println(john.toHTML(PartialUserSerializer))

    /*
      Enhancing you classes with type classes
        - type classe itself : HTMLSerializer[T] { .. }
        - type class instances (some of which are implicit): UserSerialzier, IntSerializer, ..
        - conversions with implicits classes: HTMLEnrichment
    */

    // context bounds
    def htmlBoilerPlate[T](content: T)(implicit serializer: HTMLSerializer[T]): String =
      s"<html><body>${content.toHTML(serializer)}</body></html>"

    // sweeter ! context bound tells the compiler to inject an implicit parameter of type HTMLSerialzier of type T
    def htmlSugar[T : HTMLSerializer](content: T): String =
      s"<html><body>${content.toHTML}</body></html>"

    // implicitly
    case class Permissions(mask: String)
    implicit val defaultPermission = Permissions("0744")

    // in some other part of the code
    val standardPerms = implicitly[Permissions]

    def htmlSugarAlsoWithImplicitly[T : HTMLSerializer](content: T): String =
      val serializer = implicitly[HTMLSerializer[T]]
      s"<html><body>${content.toHTML(serializer)}</body></html>"
  }

}
