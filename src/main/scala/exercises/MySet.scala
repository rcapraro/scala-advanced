package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {

  def apply(elem: A): Boolean = contains(elem)

  def contains(elem: A): Boolean

  def +(elem: A): MySet[A]

  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B]

  def filter(predicate: A => Boolean): MySet[A]

  def foreach(f: A => Unit): Unit
}

class EmptySet[A] extends MySet[A] {
  override def contains(elem: A) = false

  override def +(elem: A) = new NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]) = anotherSet

  override def map[B](f: A => B) = new EmptySet[B]

  override def flatMap[B](f: A => MySet[B]) = new EmptySet[B]

  override def filter(predicate: A => Boolean) = this

  override def foreach(f: A => Unit) = ()
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
}

object MySet {
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]) : MySet[A] =
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)
    buildSet(values.toSeq, new EmptySet[A])
  }
}

@main
def testMySet(): Unit = {

  def printElement[A](elem: A) = print(s"$elem, ")

  val s = MySet(1,2,3,4)
  s + 5 ++ MySet(0, -1, -2) + 3 foreach printElement
  println()

  s flatMap (x => MySet(x, 10 *x)) foreach printElement
  println

  s filter ( _ %2 == 0) foreach printElement
}

