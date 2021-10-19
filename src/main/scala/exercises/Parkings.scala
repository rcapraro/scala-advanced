package exercises

object Parkings {

  class Vehicle
  class Bicycle extends Vehicle
  class Car extends Vehicle

  class IList[T]

  //invariant
  class IParking[T](vehicles: List[T]) {
    def park(vehicle: T): IParking[T] = ???
    def impound(vehicles: List[T]): IParking[T] = ???
    def checkVehicles(conditions: String): List[T] = ???
    def flatMap[S](f: T => IParking[S]): IParking[S] = ???
  }

  //covariant
  class CParking[+T](vehicles: List[T]) {
    def park[S >: T](vehicle: S): CParking[S] = ???
    def impound[S >: T](vehicles: List[S]): CParking[S] = ???
    def checkVehicles(conditions: String): List[T] = ???
    def flatMap[S](f: T => IParking[S]): IParking[S] = ???
  }

  //contravariant
  class XParking[-T](vehicles: List[T]) {
    def park[T](vehicle: T): XParking[T] = ???
    def impound[T](vehicles: List[T]): XParking[T] = ???
    def checkVehicles[S <: T](conditions: String): List[S] = ???
    // f: Function1[R, XParking[S]}
    def flatMap[R <: T, S](f: R => IParking[S]): IParking[S] = ???
  }

  /*
  Rule of thumb:
    - use covariance = COLLECTION OF THINGS
    - use contravariance = GROUP OF ACTIONS
  */

  //covariant
  class CParking2[+T](vehicles: IList[T]) {
    def park[S >: T](vehicle: S): CParking2[S] = ???
    def impound[S >: T](vehicles: IList[S]): CParking2[S] = ???
    def checkVehicles[S >: T](conditions: String): IList[S] = ???
  }

  //contravariant
  class XParking2[-T](vehicles: IList[T]) {
    def park[T](vehicle: T): XParking2[T] = ???
    def impound[S <: T](vehicles: IList[S]): XParking2[S] = ???
    def checkVehicles[S <: T](conditions: String): IList[S] = ???
  }

  //flatmap

}
