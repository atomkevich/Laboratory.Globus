package com.adform.lab.controllers

import com.adform.lab.converters.Helper
import com.adform.lab.domain.Employee
import play.api.mvc._
import play.api.libs.json._
import com.adform.lab.services.EmployeeServiceComponent


/**
 * Created by Alina_Tamkevich on 2/9/2015.
 */
trait EmployeeController extends Controller with Secured{
  this: EmployeeServiceComponent =>


  implicit val employeeWrites = new Writes[Employee] {
    override def writes(employee: Employee): JsValue = {
      Json.obj(
         "id" -> employee.id,
        "name" -> employee.employeeProfile.name,
        "location" -> employee.employeeProfile.location,
        "role" -> Helper.convertRolesToString(employee.roles),
        "parentId" -> employee.parent,
        "ancestors" -> employee.ancestors
      )
    }
  }

  def getEmployeeByEMail(email: String) = {
    employeeService.findEmployeeByEmail(email)
  }

  def deleteByIds() =  Action(parse.json){request =>
    val ids = (request.body \ "ids").asOpt[List[String]]
    if (ids.isDefined) {
      employeeService.deleteEmployees(ids.get)
      Ok("Successfully deleted")
    } else {
      BadRequest("Bad request. Id of employee for delete is not present.")
    }
  }

  def createEmployee = Action(parse.json) { request =>
    val email = (request.body \ "email").asOpt[String]
    val roles = (request.body \ "roles").asOpt[List[String]].getOrElse(List("Viewer"))
    val parentId = (request.body\ "parentId").asOpt[String]
    if (email.isDefined) {
      employeeService.createNewEmployee(email.get, roles, parentId)
      Created
    } else {
      BadRequest("Missing params. Please enter employee's email.")
    }
  }

  def findEmployeeById(id: String) = Action {
    val user: Option[Employee] = employeeService.getEmployeeById(id)
    if (user.isDefined) {
      Ok(Json.toJson(user))
    } else {
      NotFound

    }
  }
    def findEmployeeByParams = Action { request =>
      val params = request.queryString.map { case (k, v) => k -> v.mkString}
      val employees = employeeService.getAllEmployees(params)
      if (!employees.isEmpty) {
        Ok(Json.toJson(employees))
      } else {
        NotFound
      }
    }

  def assignRole = Action(parse.json) { request =>
    val roles = (request.body \ "roles").asOpt[List[String]]
    val employeeId = (request.body\ "id").asOpt[String]
    if (roles.isDefined && employeeId.isDefined) {
      employeeService.assignRoles(employeeId.get, roles.get)
      Ok("Successfully updated")
    } else {
      BadRequest("Wrong params")
    }
  }

  def updateEmployeeProfile = Action(parse.json) { request =>
    val employeeId = (request.body\ "id").asOpt[String]
    val fields = request.body.asInstanceOf[JsObject].value.map { case (k, v) => k -> v.as[String]}.filter(t => !"id".equals(t._1))
    if (employeeId.isDefined) {
      employeeService.updateProfile(employeeId.get, fields.toMap)
      Ok("Successfully updated")
    } else {
      BadRequest("Wrong params")
    }
  }

  def delete(id: String) = Action {
    if (!id.isEmpty) {
      employeeService.deleteEmployees(List(id))
      Ok("Successfully updated")
    } else {
      BadRequest("Wrong params")
    }
  }

  def multiUpdate = Action(parse.json) {request =>
    val updateParams = request.body.asInstanceOf[JsObject].value.map(param => (param._1, param._2.toString))
    if (updateParams.size == 1) {
      employeeService.multiUpdate(updateParams.head)
      Ok("Successfully updated")
    } else {
      BadRequest("Wrong params")
    }
  }
}
