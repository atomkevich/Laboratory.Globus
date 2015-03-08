package com.adform.lab.controllers.spray


import com.adform.lab.domain.Employee
import com.adform.lab.services.EmployeeServiceComponent
import play.api.libs.json._
import spray.http.MediaTypes._
import spray.httpx.PlayJsonSupport._
import spray.routing.HttpService

import scala.concurrent.ExecutionContext.Implicits.global



trait EmployeesApi extends Authenticator {
  this: HttpService with EmployeeServiceComponent  =>

  implicit val employeeWrites = new Writes[Employee] {

    override def writes(employee: Employee): JsValue = {
      Json.obj(
        "id" -> employee.id,
        "profile" -> Json.obj(
          "name" -> employee.employeeProfile.name,
          "email" -> employee.employeeProfile.email,
          "yammerUrl" -> employee.employeeProfile.yammerUrl,
          "location" -> employee.employeeProfile.location
        ),
        "roles" -> employee.roles.map(_.name),
        "parentId" -> employee.parent,
        "ancestors" -> employee.ancestors
      )
    }
  }

  val employeeRoute = {
    pathPrefix("v1") {

        path("employees") {
          get {
            authenticate(userAuthorization("PODLead")) { employeeInfo =>
              parameterMap { queryParams =>
                respondWithMediaType(`application/json`) {
                  complete {
                    employeeService.getAllEmployees(queryParams)
                  }
                }
              }
            }
          } ~
          post {
            authenticate(userAuthorization("Admin", "PODLead")) { employeeInfo =>
              respondWithMediaType(`application/json`) {
                entity(as[JsObject]) { requestObj =>
                  val email = (requestObj \ "email").asOpt[String]
                  val password = (requestObj \ "password").asOpt[String].getOrElse("pass")
                  val roles = (requestObj \ "roles").asOpt[String].getOrElse("Viewer").split(",").toList
                  val parentId = (requestObj \ "parentId").asOpt[String]
                  complete {
                    if (email.isDefined) {
                      employeeService.createNewEmployee(email.get, password, roles, parentId) match {
                        case Left(message) => message
                        case Right(err) => err
                      }
                    } else {
                      "Missing params. Please enter employee's email."
                    }
                  }
                }
              }
            }
          } ~
          put {
                authenticate(userAuthorization("Admin", "PODLead")) { employeeInfo =>
                respondWithMediaType(`application/json`) {
                  entity(as[JsObject]) { requestObj =>
                    complete {
                      val updateParams = requestObj.value.map(param => (param._1, param._2.toString))
                      if (updateParams.size == 1) {
                        employeeService.multiUpdate(updateParams.head)
                        "Successfully updated"
                      } else {
                        "Wrong params"
                      }
                    }
                  }
                }
             }
          } ~
          pathPrefix("profile") {
            put {
              authenticate(userAuthorization("Admin", "PODLead", "User")) { employeeInfo =>
                  respondWithMediaType(`application/json`) {
                    entity(as[JsObject]) { requestObj =>
                      val employeeId = (requestObj \ "id").asOpt[String]
                      val fields = (requestObj \ "profile").asInstanceOf[JsObject].value
                        .map { case (k, v) => (k -> (if (v.isInstanceOf[JsString]) v.as[String] else null))}
                      complete {
                        if (employeeId.isDefined) {
                          employeeService.updateProfile(employeeId.get, fields.toMap)
                          "Successfully updated"
                        } else {
                          "Wrong params"
                        }
                      }
                    }
                  }
                }
              }
            } ~
            delete {
                authenticate(userAuthorization("Admin", "PODLead")) { employeeInfo =>
                respondWithMediaType(`application/json`) {
                  entity(as[JsObject]) { requestObj =>
                    complete {
                      val status = (requestObj \ "ids").asOpt[List[String]].map {
                        ids => employeeService.deleteEmployees(ids)
                          "Successfully deleted"
                      } getOrElse ("Bad request. Id of employee for delete is not present.")
                      status
                    }
                  }
                }
              }
            } ~
            pathPrefix("roles") {
                put {
                  authenticate(userAuthorization("Admin", "PODLead")) { employeeInfo =>
                  respondWithMediaType(`application/json`) {
                    entity(as[JsObject]) { requestObj =>
                      val roles = (requestObj \ "roles").asOpt[List[String]]
                      val employeeId = (requestObj \ "id").asOpt[String]
                      complete {
                        if (roles.isDefined && employeeId.isDefined) {
                          employeeService.assignRoles(employeeId.get, roles.get)
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
          path("employee" / Segment) { id =>
            authenticate(userAuthorization()) { employeeInfo =>
            pathEnd {
              get {
                respondWithMediaType(`application/json`) {
                  complete {
                    employeeService.getEmployeeById(id)
                  }
                }
              }
              } ~
              authenticate(userAuthorization("PODLead", "Admin", "User")) { employeeInfo =>
                delete {
                  complete {
                    employeeService.deleteEmployees(List(id))
                    "Successfully deleted"
                  }
                }
            }
         }
        }~
          path("current") {
            authenticate(userAuthorization()) { employeeInfo =>
            pathEnd {
              get {
                respondWithMediaType(`application/json`) {
                  complete {
                    "employeeInfo"
                  }
                }
              }
            }
          }
       }
      }
    }
}