package com.adform.lab.controllers.spray

import com.adform.lab.domain.{PODProfile, POD}
import com.adform.lab.services.PODServiceComponent
import play.api.libs.json._
import spray.http.MediaTypes._
import spray.httpx.PlayJsonSupport._
import spray.routing.HttpService
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by Alina_Tamkevich on 3/3/2015.
 */
trait PODApi extends Authenticator {
  this: HttpService with PODServiceComponent =>

  implicit val podWrites = new Writes[POD] {
    override def writes(pod: POD): JsValue = {
      Json.obj(
        "id" -> pod.id,
        "profile" -> Json.obj(
          "name" -> pod.podProfile.name,
          "location" -> pod.podProfile.location,
          "description" -> pod.podProfile.description
        ),
        "parentId" -> pod.parent,
        "ancestors" -> pod.ancestors
      )
    }
  }
  implicit val podReaders = new Reads[POD] {
    override def reads(json: JsValue) = JsSuccess(new POD(
      Some((json \("id")).as[String]),
      PODProfile(
        (json \ ("profile") \ ("name")).as[String],
        (json \ ("profile") \ ("location")).as[String],
        (json \ ("profile") \ ("description")).as[String]
      ),
      (json \ ("ancestors")).as[List[String]],
      (json \ ("parentId")).as[String]
    ))
  }

  val podRoute = {
    pathPrefix("v1") {
      path("pods") {
        get {
          authenticate(userAuthorization("PODLead")) { employeeInfo =>
            parameterMap { queryParams =>
              respondWithMediaType(`application/json`) {
                complete {
                  podService.getPODs(queryParams)
                }
              }
            }
          }
        } ~
        post {
          authenticate(userAuthorization("Admin")) { employeeInfo =>
            respondWithMediaType(`application/json`) {
              entity(as[JsObject]) { requestObj =>
                val name = (requestObj \ "name").asOpt[String]
                val location = (requestObj \ "location").asOpt[String].getOrElse("")
                val description = (requestObj \ "description").asOpt[String].getOrElse("")
                val parentId = (requestObj \ "parentId").asOpt[String]
                complete {
                  if (name.isDefined) {
                    podService.createPOD(parentId, name.get, location, description)
                    "Created"
                  } else {
                    "Missing params"
                  }
                }
              }
            }
           }
          } ~
          pathPrefix("parent" / Segment) { id =>
            authenticate(userAuthorization()) { employeeInfo =>
              pathEnd {
                get {
                  respondWithMediaType(`application/json`) {
                    complete {
                      podService.getParentPOD(id)
                    }
                  }
                }
              }
            }
          } ~
          pathPrefix("linked" / Segment) { id =>
            authenticate(userAuthorization()) { employeeInfo =>
              pathEnd {
                get {
                  respondWithMediaType(`application/json`) {
                    complete {
                      podService.getPODLinksById(id)
                    }
                  }
                }
              }
            }
          } ~
          pathPrefix("childs" / Segment) { id =>
            authenticate(userAuthorization()) { employeeInfo =>
              pathEnd {
                get {
                  respondWithMediaType(`application/json`) {
                    complete {
                      podService.getPODChildsById(id)
                    }
                  }
                }
              }
            }
          } ~
          pathPrefix("profile") {
            put {
              authenticate(userAuthorization()) { employeeInfo =>
                respondWithMediaType(`application/json`) {
                  entity(as[JsObject]) { requestObj =>
                    val podId = (requestObj \ "id").asOpt[String]
                    val fields = (requestObj \ "profile").asInstanceOf[JsObject].value
                      .map { case (k, v) => (k -> (if (v.isInstanceOf[JsString]) v.as[String] else null))}
                    complete {
                      if (podId.isDefined) {
                        podService.updateProfile(podId.get, fields.toMap)
                        "Successfully updated"
                      } else {
                        "Wrong params"
                      }
                    }
                  }
                }
              }
            }
          }
      } ~
        path("pod" / Segment) { id =>
          pathEnd {
            get {
              authenticate(userAuthorization()) { employeeInfo =>
                respondWithMediaType(`application/json`) {
                  complete {
                    podService.getPODById(id)
                  }
                }
              }
            } ~
            delete {
              authenticate(userAuthorization("Admin")) { employeeInfo =>
                complete {
                  podService.deletePODs(List(id))
                  "Successfully deleted"
                }
              }
            }
          }
        }
    }
  }
}
