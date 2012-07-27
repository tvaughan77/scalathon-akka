import akka.actor._
import akka.routing.RoundRobinRouter
import akka.util.Duration
import akka.util.duration._

/**
 * <p>Code (slightly modified) from the Intro Guide to Akka found: 
 * http://doc.akka.io/docs/akka/2.0.2/intro/getting-started-first-scala.html</p>
 * 
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

/**
 * This Worker class is an Actor that knows how to calculate Pi using the formula:
 * 
 * SUM[n=0 --> INFINITY] of (-1)^n / (2n + 1)
 * 
 * To divide and conquer the domain space, this actor can take a starting index (n) and some 
 * number of elements to compute (e.g. the Nth through the N+numElements values of the equation)
 */
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

/**
 * The Master
 * @param numWorkers - how many Worker actors to setup
 * @param numMessages - how many number chunks to send out to the workers
 * @param numElements - how big each number chunk is sent to each worker
 */
class Master(numWorkers: Int, numMessages: Int, numElements: Int, listener: ActorRef) extends Actor {
  var pi: Double = _
  var numResults: Int = _
  val start: Long = System.currentTimeMillis
  
  val workerRouter = context.actorOf(
    Props[Worker].withRouter(RoundRobinRouter(numWorkers)), name="workerRouter")
  
  def receive = {
    case Calculate =>
      for (i <- 0 until numMessages) workerRouter ! Work(i * numElements, numElements)
    case Result(value) =>
      pi += value
      numResults += 1
      if(numResults == numMessages) {   // if every message we've sent out has returned, we're done.  Shut down the shop.
        listener ! PiApproximation(pi, runTime = (System.currentTimeMillis - start).millis)
        context.stop(self)
      }
  }
}
