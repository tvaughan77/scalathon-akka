import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.junit.Assert._
import akka.actor.ActorSystem
import akka.testkit._


@RunWith(classOf[JUnitRunner])
class PiTestSuite extends TestKit(ActorSystem("test")) with FunSuite {
  
  val FLOAT_DELTA = 0.0001
  val workerRef = TestActorRef[Worker]
  val worker = workerRef.underlyingActor
  
  test("A worker calculating pi starting at 0 for 1 places yields 1") {
    assertEquals(1, worker.calculatePiFor(0, 1), FLOAT_DELTA)
  }
  
  test("A worker calculating pi starting at 0 for 2 places yields 1 - 1/3 = 2/3") {
    assertEquals((1.0 - 1.0/3.0), worker.calculatePiFor(0, 2), FLOAT_DELTA)
  }
  
  test("A worker calculating pi starting at 1 for 3 places yields -1/3 + 1/5 - 1/7") {
    assertEquals(-(1.0 / 3.0) + (1.0 / 5.0) - (1.0 / 7.0), worker.calculatePiFor(1, 3), FLOAT_DELTA)
  }
}
