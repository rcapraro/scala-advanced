package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {

  def apply(elem: A): Boolean = contains(elem)

  def contains(elem: A): Boolean

  def +(elem: A): MySet[A] //add

  def ++(anotherSet: MySet[A]): MySet[A] //union

  def map[B](f: A => B): MySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B]

  def filter(predicate: A => Boolean): MySet[A]

  def foreach(f: A => Unit): Unit

  def -(elem: A): MySet[A] //remove

  def --(anotherSet: MySet[A]): MySet[A] //difference

  def &(anotherSet: MySet[A]): MySet[A] //intersection

  def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A] {
  override def contains(elem: A) = false

  override def +(elem: A) = new NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]) = anotherSet

  override def map[B](f: A => B) = new EmptySet[B]

  override def flatMap[B](f: A => MySet[B]) = new EmptySet[B]

  override def filter(predicate: A => Boolean) = this

  override def foreach(f: A => Unit) = ()

  override def -(elem: A): MySet[A] = this

  override def --(anotherSet: MySet[A]) = this

  override def &(anotherSet: MySet[A]) = this

  override def unary_! = new PropertyBasedSet[A](_ => true)
}

// all elements of type A which satisfy a property
// { x in A | property(x) }
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  override def contains(elem: A) = property(elem)

  // { x in A | property(x) } + element = { x in A | property(x) || x == elem }
  override def +(elem: A) = new PropertyBasedSet[A](x => property(x) || x == elem)

  // { x in A | property(x) } ++ anotherSet = { x in A | property(x) || anotherSet contains x }
  override def ++(anotherSet: MySet[A]) = new PropertyBasedSet[A](x => property(x) || anotherSet(x))

  // impossible ex: alla integers => (_ % 3) => [0 1 2]
  override def map[B](f: A => B) = politefail

  override def flatMap[B](f: A => MySet[B]) = politefail

  override def foreach(f: A => Unit) = politefail

  override def filter(predicate: A => Boolean) = new PropertyBasedSet[A](x => property(x) && predicate(x))

  override def -(elem: A): MySet[A] = filter(x => x != elem)

  override def --(anotherSet: MySet[A]) = filter(!anotherSet)

  override def &(anotherSet: MySet[A]) = filter(anotherSet)

  override def unary_! = new PropertyBasedSet[A](x => !property(x))

  def politefail = throw new IllegalArgumentException("Readly deep rabbit hole")
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {

  override def contains(elem: A) =
    elem == head || tail.contains(elem)

  override def +(elem: A) =
    if (this.contains(elem)) this
    else new NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]) =
    tail ++ anotherSet + head

  override def map[B](f: A => B) = (tail map f) + f(head)

  override def flatMap[B](f: A => MySet[B]) = (tail flatMap f) ++ f(head)

  override def filter(predicate: A => Boolean) = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  override def foreach(f: A => Unit) = {
    f(head)
    tail foreach f
  }

  override def -(elem: A): MySet[A] =
    if (head == elem) tail
    else tail - elem + head

  override def --(anotherSet: MySet[A]) = filter(x => !anotherSet(x))

  override def &(anotherSet: MySet[A]) = filter(anotherSet) //intersection = filtering

  override def unary_! = new PropertyBasedSet[A](x => !this.contains(x))
}

object MySet {
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)

    buildSet(values.toSeq, new EmptySet[A])
  }
}

@main
def testMySet(): Unit = {

  def printElement[A](elem: A) = print(s"$elem, ")

  val s = MySet(1, 2, 3, 4)
  s + 5 ++ MySet(0, -1, -2) + 3 foreach printElement
  println()

  s flatMap (x => MySet(x, 10 * x)) foreach printElement
  println

  s filter (_ % 2 == 0) foreach printElement
  println

  val negative = !s //s.unary_!
  println(negative(3)) //false
  println(negative(5)) //true

  val negativeEven = negative.filter(_ % 2 == 0)
  println(negativeEven(5)) //false

  val negativeEven5 = negativeEven + 5 //all the even number bigger than 4 plsu the number 5
  println(negativeEven5(5)) //true
}

