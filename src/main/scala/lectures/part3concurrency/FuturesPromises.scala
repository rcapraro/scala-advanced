package lectures.part3concurrency

import java.awt.Taskbar.Feature
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success}
import scala.util.Try

object FuturesPromises {

  @main
  def testFuturesPromises(): Unit = {

    def calculateMeaningOfLife: Int = {
      Thread.sleep(2000)
      42
    }

    val aFuture = Future {
      calculateMeaningOfLife // calculates the meaning of life on another thread
    }

    println(aFuture.value) // Option[Try[Int]]
    println("waiting on the future")
    aFuture.onComplete {
      case Success(meaningOfLife) => println(s"The meaning of life is $meaningOfLife")
      case Failure(exception) => print(s"I have failed with $exception")
    } // SOME thread

    Thread.sleep(2500) //wait future completion

    // mini social network
    case class Profile(id: String, name: String) {
      def poke(anotherProfile: Profile) = println(s"${this.name} poking ${anotherProfile.name}")
    }

    object SocialNetwork {
      // database of profiles
      val names = Map(
        "fb.id.1-zuck" -> "Mark",
        "fb.id.2-bill" -> "Bill",
        "fb-id.0-dummy" -> "Dummy"
      )
      val friends = Map(
        "fb.id.1-zuck" -> "fb.id.2-bill"
      )
      val random = new Random()

      // API
      def fetchProfile(id: String): Future[Profile] = Future {
        Thread.sleep(random.nextInt(300))
        Profile(id, names(id))
      }

      def fetchBestFriend(profile: Profile): Future[Profile] = Future {
        Thread.sleep(random.nextInt(400))
        val bfId = friends(profile.id)
        Profile(bfId, names(bfId))
      }
    }

    // client: mark to poke bill
    val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
    /*
    mark.onComplete {
      case Success(markProfile) => {
        val bill = SocialNetwork.fetchBestFriend(markProfile)
        bill.onComplete {
          case Success(billProfile) => markProfile.poke(billProfile)
          case Failure(e) => e.printStackTrace()
        }
      }
      case Failure(e) => e.printStackTrace()
    }
    */

    // better way: functional composition of Futures
    // map, flatMap, filter
    val nameOnTheWall = mark.map(profile => profile.name)
    val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
    val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))

    // for comprehension
    for {
      mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
      bill <- SocialNetwork.fetchBestFriend(mark)
    } mark.poke(bill)

    Thread.sleep(800)

    // fallbacks
    val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknow id").recover {
      case e: Throwable => Profile("fb-id.0-dummy", "Forever alone")
    }

    val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recoverWith {
      case e: Throwable => SocialNetwork.fetchProfile("fb-id.0-dummy")
    }

    val fallbackResult = SocialNetwork.fetchProfile("unknow id").fallbackTo(SocialNetwork.fetchProfile("fb-id.0-dummy"))

    // small online banking app
    case class User(name: String)
    case class Transaction(sender: String, receiver: String, amount: Double, status: String)

    object BankingApp {
      val name = "Rock the JVM banking"

      def fetchUser(name: String): Future[User] = Future {
        // simulates fetching from the DB
        Thread.sleep(500)
        User(name)
      }

      def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
        // simulates some processes
        Thread.sleep(1000)
        Transaction(user.name, merchantName, amount, "Success")
      }

      def purchase(userName: String, item: String, merchantName: String, cost: Double): String = {
        // fetch the user from the DB
        // create a transaction
        // wait for the transaction to finish!
        val transactionStatusFuture = for {
          user <- fetchUser(userName)
          transaction <- createTransaction(user, merchantName, cost)
        } yield transaction.status

        // blocks until the transaction is complete
        Await.result(transactionStatusFuture, 2.seconds) //implicit conversions -> pimp my library
      }
    }

    println(BankingApp.purchase("Richard", "iPhone 12", "rock the jvm store", 3000))

    // promises
    val promise = Promise[Int]() // "controller over a future
    val future = promise.future

    // thread 1 - "consumer"
    future.onComplete {
      case Success(result) => println(s"[consumer] I've received $result")
      case Failure(exception) => ()
    }

    // thread 2 - producer
    val producer = new Thread(() => {
      println("[producer] crunching numbers...")
      Thread.sleep(500)
      // "fulfilling" the promise
      promise.success(42) // could be promise.failure(exception)
      println("[producer] done")
    })

    producer.start()
    Thread.sleep(1000)

    def fulfillImmediatly[T](value: T): Future[T] = Future.successful(value)

    def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] =
      first.flatMap(_ => second)

    def first[T](fa: Future[T], fb: Future[T]): Future[T] = {
      val promise = Promise[T]
      fa.onComplete(promise.tryComplete)
      fb.onComplete(promise.tryComplete)
      promise.future
    }

    def last[T](fa: Future[T], fb: Future[T]): Future[T] = {
      // 1 promise which both futures will try to complete
      // 2 promise which the LAST future will complete
      val bothPromise = Promise[T]
      val lastPromise = Promise[T]

      val checkAndComplete = (result: Try[T]) => if(!bothPromise.tryComplete(result)) lastPromise.complete(result)

      fa.onComplete(checkAndComplete)
      fb.onComplete(checkAndComplete)
      lastPromise.future
    }

    val fast = Future {
      Thread.sleep(100)
      "fast"
    }

    val slow = Future {
      Thread.sleep(200)
      "slow"
    }

    first(fast, slow).foreach(println)
    last(fast, slow).foreach(println)

    def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T] =
      action()
        .filter(condition)
        .recoverWith {
          case _ => retryUntil(action, condition)
        }

    val random = new Random()
    val action = () => Future {
      Thread.sleep(100)
      val nextValue = random.nextInt(100)
      println("generated " +  nextValue)
      nextValue
    }
    retryUntil(action, (x: Int) => x > 90).foreach(result => println("settled at " + result))

    Thread.sleep(10000)
  }

}
