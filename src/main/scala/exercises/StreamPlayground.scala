package exercises

import scala.annotation.tailrec

/*
Implements a lazily evaluated, singly linked STREAM of elements
naturals = MyStream.from(1)(x => x +1) = stream of natural numbers (potentially infinite)
naturals.take(100).foreach(println) // lazily evaluated stream of the first 100 naturals (finite stream)
naturals.foreach(println) // will crash - infinite!
naturals.map(_ * 2) //stream of all even numbers (potentially infinite)
*/
abstract class MyStream[+A] {
  def isEmpty: Boolean

  def head: A

  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B] //prepend operator

  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] //concatenates two streams

  def foreach(f: A => Unit): Unit

  def map[B](f: A => B): MyStream[B]

  def flatMap[B](f: A => MyStream[B]): MyStream[B]

  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] //takes the first n elements out of the stream

  def takeAsList(n: Int): List[A] = take(n).toList()

  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] =
    if (isEmpty) acc.reverse
    else tail.toList(head :: acc)
}

object EmptyStream extends MyStream[Nothing] {
  override def isEmpty = true

  override def head = throw new NoSuchElementException

  override def tail = throw new NoSuchElementException

  override def #::[B >: Nothing](element: B) = new Cons(element, this)

  override def ++[B >: Nothing](anotherStream: => MyStream[B]) = anotherStream

  override def foreach(f: Nothing => Unit) = ()

  override def map[B](f: Nothing => B) = this

  override def flatMap[B](f: Nothing => MyStream[B]) = this

  override def filter(predicate: Nothing => Boolean) = this

  override def take(n: Int) = this
}

class Cons[+A](h: A, t: => MyStream[A]) extends MyStream[A] {
  override def isEmpty = false

  override val head = h

  override lazy val tail = t //call by need

  override def #::[B >: A](element: B) = new Cons(element, this)

  override def ++[B >: A](anotherStream: => MyStream[B]) = new Cons(head, tail ++ anotherStream)

  override def foreach(f: A => Unit) = {
    f(head)
    tail foreach f
  }

  override def map[B](f: A => B) = new Cons(f(head), tail map f) //preserve lazy evalutation

  override def flatMap[B](f: A => MyStream[B]) = f(head) ++ (tail flatMap f) //preserve lazy evalutation

  override def filter(predicate: A => Boolean) =
    if (predicate(head)) new Cons(head, tail filter predicate)
    else tail filter predicate //preserve lazy evalutation

  override def take(n: Int) =
    if (n <= 0) EmptyStream
    else if (n == 1) new Cons(head, EmptyStream)
    else new Cons(head, tail.take(n - 1)) //preserve lazy evalutation

}

object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] =
    new Cons(start, MyStream.from(generator(start))(generator))
}

object StreamPlayground {

  @main def testMyStream(): Unit = {
    val naturals = MyStream.from(1)(_ + 1)
    println(naturals.head)
    println(naturals.tail.head)
    println(naturals.tail.tail.head)

    val startFrom0 = 0 #:: naturals // naturals.#::(0)
    println(startFrom0.head)

    startFrom0.take(1000).foreach(println)

    //map, flatmap
    println(startFrom0.map(_ * 2).take(100).toList())
    println(startFrom0.flatMap(x => new Cons(x, new Cons(x + 1, EmptyStream))).take(10).toList())
    println(startFrom0.filter(_ < 10).take(10).toList())

    /*
    stream of fibonacci numbers
    [ first,  [...
    [ first, fibo(second, first+second)
    */
    def fibonacci(first: BigInt, second: BigInt): MyStream[BigInt] =
      new Cons(first, fibonacci(second, first + second))

    /*
    stream of prime numbers with Erastosthenes's sieve
    [2 3 4 5 6 7 8...]
    filter out all numbers divisible by 2 and keep 2
    [2 3 5 7 9 11 13 ...]
    filter out all numbers divisible by 3
    [2 5 7 11 13 17 19...]
    filter out all number divisible by 5
    [2 7 11 13 17 19 23...]
    filter out all number divisible by 7
    etc...
    */
    def erastosthenes(numbers: MyStream[Int]): MyStream[Int] =
      if (numbers.isEmpty) numbers
      else new Cons(numbers.head, erastosthenes(numbers.tail.filter( _ % numbers.head !=0 )))

    println(fibonacci(1, 1).take(100).toList())

    println(erastosthenes(MyStream.from(2)(_ +1)).take(100).toList())

  }

}
