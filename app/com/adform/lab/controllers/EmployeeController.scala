package com.adform.lab.controllers

import com.adform.lab.converters.Helper
import com.adform.lab.domain.Employee
import com.adform.lab.repositories.{PodRepositoryComponentImpl, EmployeeRepositoryComponentImpl}
import play.api.mvc._
import play.api.libs.json._
import com.adform.lab.services.{PODServiceComponentImpl, EmployeeProfileServiceComponentImpl, EmployeeServiceComponentImpl, EmployeeServiceComponent}


/**
 * Created by Alina_Tamkevich on 2/9/2015.
 */
object EmployeeController extends Controller
                            with Secured  with EmployeeServiceComponentImpl
                            with EmployeeProfileServiceComponentImpl  with EmployeeRepositoryComponentImpl
                            with PODServiceComponentImpl  with PodRepositoryComponentImpl{
  this: EmployeeServiceComponent =>


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
        "roles" -> Helper.convertRolesToString(employee.roles),
        "parentId" -> employee.parent,
        "ancestors" -> employee.ancestors
      )
    }
  }

  def getEmployeeByEMail(email: String) = {
    employeeService.findEmployeeByEmail(email)
  }

  def deleteByIds() =  HasAnyRole("Admin")(parse.json){employee => request =>
      (request.body \ "ids").asOpt[List[String]].map {ids =>
      employeeService.deleteEmployees(ids)
      Ok("Successfully deleted")
    } getOrElse(BadRequest("Bad request. Id of employee for delete is not present."))
  }


  def createEmployee = Action(parse.json) {  request =>
    val email = (request.body \"email").asOpt[String]
    val roles = (request.body \ "roles").asOpt[String].getOrElse("Viewer").split(",").toList
    val parentId = (request.body\ "parentId").asOpt[String]
    if (email.isDefined) {
      employeeService.createNewEmployee(email.get, roles, parentId)
      Created
    } else {
      BadRequest("Missing params. Please enter employee's email.")
    }
  }

  def currentEmployee = WithAuthentication { employee => request =>
    Ok(Json.toJson(employee))
  }

  def findEmployeeById(id: String) = WithAuthentication { employee => request =>
    val user: Option[Employee] = employeeService.getEmployeeById(id)
    if (user.isDefined) {
      Ok(Json.toJson(user))
    } else {
      NotFound

    }
  }
    def findEmployeeByParams = WithAuthentication { employee => implicit request =>
      val params = request.queryString.map { case (k, v) => k -> v.mkString}
      val employees = employeeService.getAllEmployees(params)
      Ok(Json.toJson(employees))
    }

  def assignRole = HasRole("Admin")(parse.json) { employee => request =>
    val roles = (request.body \ "roles").asOpt[List[String]]
    val employeeId = (request.body\ "id").asOpt[String]
    if (roles.isDefined && employeeId.isDefined) {
      employeeService.assignRoles(employeeId.get, roles.get)
      Ok("Successfully updated")
    } else {
      BadRequest("Wrong params")
    }
  }

  def updateEmployeeProfile = HasAnyRole("PODKeeper", "PODLead", "User", "Admin") (parse.json) { employee => request =>
    val employeeId = (request.body\ "id").asOpt[String]
    val fields = request.body.asInstanceOf[JsObject].value.map { case (k, v) => k -> v.as[String]}.filter(t => !"id".equals(t._1))
    if (employeeId.isDefined) {
      employeeService.updateProfile(employeeId.get, fields.toMap)
      Ok("Successfully updated")
    } else {
      BadRequest("Wrong params")
    }
  }

  def delete(id: String) = HasAnyRole("PODKeeper", "PODLead", "User", "Admin")(parse.json) { employee => request =>
    if (!id.isEmpty) {
      employeeService.deleteEmployees(List(id))
      Ok("Successfully updated")
    } else {
      BadRequest("Wrong params")
    }
  }

  def multiUpdate = HasRole("Admin")(parse.json) {employee => request =>
    val updateParams = request.body.asInstanceOf[JsObject].value.map(param => (param._1, param._2.toString))
    if (updateParams.size == 1) {
      employeeService.multiUpdate(updateParams.head)
      Ok("Successfully updated")
    } else {
      BadRequest("Wrong params")
    }
  }
}
