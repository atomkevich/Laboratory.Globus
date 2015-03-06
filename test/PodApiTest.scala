
import com.adform.lab.controllers.spray.{ServiceApi, HttpServiceActor}
import com.adform.lab.domain.POD
import org.scalatest.{Matchers, FlatSpec}
import org.specs2.mutable.Specification
import play.api.libs.json.{Json, JsValue, Writes}
import spray.routing.HttpService
import spray.testkit.{ScalatestRouteTest, Specs2RouteTest}

import spray.http.StatusCodes._
import java.sql.Timestamp



/**
 * Created by Alina_Tamkevich on 3/4/2015.
 */


class PodApiTest extends FlatSpec with Matchers with ScalatestRouteTest with ServiceApi {
  def actorRefFactory = system


  it should "respond on empty route" in {
    Get("/v1/pods") ~> route ~> check {
        val podResponse = responseAs[String]
        assert(!podResponse.isEmpty)
        assert(podResponse.contains("name"))
    }
  }

 }
