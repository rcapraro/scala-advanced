package lectures.part4implicits

import java.util.Date

object JSONSerialization {

  /*
    Users, posts, feeds,
    Serialize to JSON
  */

  case class User(name: String, age: Int, email: String)

  case class Post(content: String, createdAt: Date)

  case class Feed(user: User, posts: List[Post])

  /*
    1 - intermediate data types (Int, String, List, Date)
    2 - type classes for conversion to intermediate data types
    3 - serialize to JSON
  */

  sealed trait JSONValue { //intermediate data type
    def stringify: String
  }

  final case class JSONString(value: String) extends JSONValue {
    override def stringify: String = "\"" + value + "\""
  }
  final case class JSONNumber(value: Int) extends JSONValue {
    override def stringify: String = value.toString
  }
  final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    override def stringify: String = values.map(_.stringify).mkString("[", ",", "]")
  }
  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    override def stringify = values.map {
      case (key, value) => "\"" + key + "\":" + value.stringify
    }.mkString("{", ",", "}")
  }

  // type class

  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }

  // type class instances

  // existing data types
  implicit object StringConverter extends JSONConverter[String] {
    override def convert(value: String): JSONValue = JSONString(value)
  }

  implicit object NumberConverter extends JSONConverter[Int] {
    override def convert(value: Int): JSONValue = JSONNumber(value)
  }

  // custom data types
  implicit object UserConverter extends JSONConverter[User] {
    override def convert(user: User): JSONValue = JSONObject(Map(
      "name" -> JSONString(user.name),
      "age" -> JSONNumber(user.age),
      "email" -> JSONString(user.email)
    ))
  }

  // conversion: pimp library
  implicit class JSONOps[T](value: T) {
    def toJSON(implicit converter: JSONConverter[T]): JSONValue = converter.convert(value)
  }

  implicit object PostConverter extends JSONConverter[Post] {
    override def convert(post: Post): JSONValue = JSONObject(Map(
      "content" -> JSONString(post.content),
      "created" -> JSONString(post.createdAt.toString)
    ))
  }

  implicit object FeedConverter extends JSONConverter[Feed] {
    override def convert(feed: Feed): JSONValue = JSONObject(Map(
      "user" -> feed.user.toJSON,
      "posts" -> JSONArray(feed.posts.map(_.toJSON))
    ))
  }

  @main
  def testJSONSerialization(): Unit = {

    val data = JSONObject(Map(
      "user" -> JSONString("Richard"),
      "posts" -> JSONArray(List(
        JSONString("Scala Rocks!"),
        JSONNumber(453)
      ))
    ))

    println(data.stringify)

    val now = new Date(System.currentTimeMillis())
    val john = User("john", 32, "john@rockthejvm.com")
    val feed = Feed(john, List(
      Post("hello", now),
      Post("look at this cute puppy", now)
    ))

    println(feed.toJSON.stringify)
  }

}
