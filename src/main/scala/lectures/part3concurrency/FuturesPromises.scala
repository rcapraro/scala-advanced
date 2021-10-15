package lectures.part3concurrency

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

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

  }

}
