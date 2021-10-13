package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro {

  @main
  def main_intro(): Unit = {
    // JVM threads

    // interface Runnable has a method run()
    val aThread = new Thread(new Runnable {
      override def run(): Unit = println("Running in parallel")
    })

    // create a JVM thread => OS thread
    // aThread.start() // gives the signal to the JVM to start a JVM thread

    // aThread.join() // blocks until a thread finishes running

    val threadHello = new Thread(() => (1 to 5).foreach(_ => println("Hello")))
    val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("Goodbye")))

    // threadHello.start()
    // threadGoodbye.start()
    // different runs produce different results

    // executors - reuse threads / thread pools
    // val pool = Executors.newFixedThreadPool(10)
    // pool.execute(() => println("Something in the thread pool"))

/*    pool.execute(() => {
      Thread.sleep(1000)
      println("Done after 1 second")
    })*/

/*    pool.execute(() => {
      Thread.sleep(1000)
      println("Almost done")
      Thread.sleep(1000)
      println("Done after 2 seconds")
    })*/

    /*
    pool.shutdown()
    pool.execute(() => println("Should not appear")) // throws an exception in the calling thread
    */

    // pool.shutdownNow()

    def runInParallel = {
      var x = 0
      val thread1 = new Thread(() => x = 1)
      val thread2 = new Thread(() => x = 2)

      thread1.start()
      thread2.start()
      println(x)
    }

    // race condition
    // for(_ <- 1 to 100) runInParallel


    class BankAccount(var amount: Int) {
      override def toString: String = "" + amount
    }

    def buy(account: BankAccount, thing: String, price: Int) =
      account.amount -= price // ! race conditin possible here

    for (_ <- 1 to 1000)
      val account = new BankAccount(50000)
      val thread1 = new Thread(() => buy(account, "shoes", 3000))
      val thread2 = new Thread(() => buy(account, "iphone 12", 4000))
      thread1.start()
      thread2.start()
      Thread.sleep(100)
      if account.amount != 43000 then println("AHA: " + account.amount) //race condition

    // option 1 : use synchronized
    def buySage(account: BankAccount, thing: String, price: Int) =
      account.synchronized { //no two threads can evaluate this at the same time
        account.amount -= price
      }

    // option 2: use @volatile on BankAccount
    class BankAccountVolatile(@volatile var amount: Int) {
      override def toString: String = "" + amount
    }


  }

}
