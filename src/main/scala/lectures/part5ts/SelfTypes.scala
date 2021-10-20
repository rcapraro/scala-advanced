package lectures.part5ts

import java.awt.Component

object SelfTypes {

  //requiring a type to be mixed in

  trait Instrumentalist {
    def play(): Unit
  }

  trait Singer {
    self: Instrumentalist => //SELF TYPE - whoever implements Singer to implement Instrumentalist
    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist {
    override def play(): Unit = ???

    override def sing(): Unit = ???
  }

  /*
  class Vocalist extends Singer {  // ILLEGAL
    override def sing(): Unit = ???
  }
  */

  val jamesHetfield = new Singer with Instrumentalist {
    override def sing(): Unit = ???

    override def play(): Unit = ???
  }

  class Guitarist extends Instrumentalist {
    override def play(): Unit = println("(guitar solo)")
  }

  val ericClapton = new Guitarist with Singer {
    override def sing(): Unit = ???
  }

  // vs inheritance

  class A

  class B extends A // B is an A

  trait T

  trait S {
    self: T =>} // S requires a T

  // CAKE PATTERN =>  "dependency injection"

  // Classical DI
  class Component {
    // API
  }

  class ComponentA extends Component

  class ComponentB extends Component

  class DependentComponent(val component: Component)

  // CAKE PATTERN
  trait ScalaComponent {
    // API
    def action(x: Int): String
  }

  trait ScalaDependentComponent {
    self: ScalaComponent =>
    def dependentAction(x: Int): String = action(x) + " this rocks"
  }

  trait ScalaApplication {
    self: ScalaDependentComponent with ScalaComponent => }

  // layer 1 - small components
  trait Picture extends ScalaComponent

  trait Stats extends ScalaComponent

  // layer 2 - compose
  trait Profile extends ScalaDependentComponent with Picture

  trait Analytics extends ScalaDependentComponent with Stats

  // layer 3 - app
  trait AnalyticApp extends ScalaApplication with Analytics

  // the cake pattern allows cyclical dependencies
  /*
  class X extends Y
  class Y extends X  // does not compile!
  */

  trait X {
    self: Y => }

  trait Y {
    self: X => }

}
