import akka.actor._
import akka.routing.RoundRobinRouter
import akka.util.Duration
import akka.util.duration._

/**
 * <p>We're going to have 3 Actors sending messages around:<br/>
 * <pre>
 *            [Master]        ---- PiApproximation ---->      [Listener]
 *             /  | \
 *            v   |  ^
 *           /    |   \
 *         Work       ^
 *          |       Result
 *          v          |
 *        [Worker Actors]
 * </pre>
 *  
 */
sealed trait PiMessage                    // sealed to avoid creating messages beyond our control
case object Calculate extends PiMessage   
case class Work(start: Int, numElements: Int) extends PiMessage
case class Result(value: Double) extends PiMessage
case class PiApproximation(piValue: Double, runTime: Duration)

class Pi {

}


class Worker extends Actor {
  def receive = {
    case Work(start, numElements) => 
      sender ! Result(calculatePiFor(start, numElements))   // sender reference is implicitly passed along with the message
  }
  
  def calculatePiFor(start: Int, numElements: Int): Double = {
    require(start >= 0)
    def calc(elem: Int, numElements: Int, accumulatedResult: Double): Double = {
      if(numElements == 0)
        accumulatedResult
      else {
        val x = Math.pow(-1, elem) / (2 * elem + 1) 
        calc(elem + 1, numElements - 1, accumulatedResult + x)
      }
    }
    calc(start, numElements, 0.0)
  }
}
