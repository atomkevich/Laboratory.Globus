import com.adform.lab.controllers.spray.PODApi
import com.adform.lab.repositories.{PodRepositoryComponentImpl, EmployeeRepositoryComponentImpl}
import com.adform.lab.services._
import spray.routing.HttpService
import spray.testkit.ScalatestRouteTest
import org.scalatest._

/**
 * Created by Alina_Tamkevich on 3/4/2015.
 */
class PodApiTest extends FlatSpec with Matchers with ScalatestRouteTest with HttpService with PODApi with EmployeeServiceComponentImpl
                                                                                  with EmployeeProfileServiceComponentImpl
                                                                                  with EmployeeRepositoryComponentImpl
                                                                                  with PODServiceComponentImpl
                                                                                  with PodRepositoryComponentImpl {

  implicit def actorRefFactory = system

  var location: String = ""

  it should "respond on empty route" in {
    Get("/employees") ~>  podRoute ~> check {
     // status == "OK"
    }
  }

}
