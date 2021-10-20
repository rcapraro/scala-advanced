package lectures.part5ts

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object HigherKindedTypes {

  @main
  def testHigherKindedTypes(): Unit = {

    trait AHigherKindedType[F[_]]

    trait MyList[T] {
      def flatMap[B](f: T => B): MyList[B]
    }

    trait MyOption[T] {
      def flatMap[B](f: T => B): Option[B]
    }

    trait MyFuture[T] {
      def flatMap[B](f: T => B): MyFuture[B]
    }

    // combine/multiply List(1, 2) x List("a", "b") => List(1a, 1b, 2a, 2b)

    def multiplyL[A, B](listA: List[A], listB: List[B]): List[(A, B)] = {
      for {
        a <- listA
        b <- listB
      } yield (a, b)
    }

    def multiplyO[A, B](optionA: Option[A], optionB: Option[B]): Option[(A, B)] = {
      for {
        a <- optionA
        b <- optionB
      } yield (a, b)
    }

    def multiplyF[A, B](futureA: Future[A], futureB: Future[B]): Future[(A, B)] = {
      for {
        a <- futureA
        b <- futureB
      } yield (a, b)
    }

    // use HKT
    trait Monad[F[_], A] { // higher-kinded type class
      def flatMap[B](f: A => F[B]): F[B]

      def map[B](f: A => B): F[B]
    }

    implicit class MonadList[A](list: List[A]) extends Monad[List, A] {
      override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)

      override def map[B](f: A => B): List[B] = list.map(f)
    }

    implicit class MonadOption[A](option: Option[A]) extends Monad[Option, A] {
      override def flatMap[B](f: A => Option[B]): Option[B] = option.flatMap(f)

      override def map[B](f: A => B): Option[B] = option.map(f)
    }

    def multiply[F[_], A, B](implicit ma: Monad[F, A], mb: Monad[F, B]): F[(A, B)] = {
      for {
        a <- ma
        b <- mb
      } yield (a, b)
      // ma.flatpMap(a => mb.map(b => (a, b)))
    }

    val monadList = new MonadList(List(1, 2, 3))
    monadList.flatMap(x => List(x, x + 1)) // Monad[List, Int] => List[Int]
    monadList.map(_ * 2) //  Monad[List, Int] => List[Int]

    println(multiply(List(1, 2), List("a", "b"))) // and no MonadList(List(1,2)) thx to the implicits !

    println(multiply(Some(1), Some("scala")))

  }
}
