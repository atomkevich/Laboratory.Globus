package com.adform.lab.controllers

/**
 * Created by Alina_Tamkevich on 2/16/2015.
 */

import com.adform.lab.controllers.Authentication._
import com.adform.lab.domain.Employee
import play.api.libs.json.JsValue
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current
/**
 * Provide security features
 */
trait Secured {
  /**
   * Not authorized, forward to login
   */
  private def onUnauthorized(request: RequestHeader) = {
    Results.Redirect(routes.Authentication.login)
  }

  def WithAuthentication(f: => Employee => Request[AnyContent] => Result) =
    Action( request =>
      request.session.get("email").flatMap(Cache get _) match {
        case Some(employee: Employee) => f(employee)(request)
        case None => onUnauthorized(request)
      }
    )
  def WithAuthentication(b: BodyParser[JsValue])
                     (f: => Employee => Request[JsValue] => Result) = {
    Action(b)( request =>
      request.session.get("email").flatMap(Cache get _) match {
        case Some(employee: Employee) => f(employee)(request)
        case None => onUnauthorized(request)
      })
  }

  def HasRole(role: String)(b: BodyParser[JsValue])(f: => Employee => Request[JsValue] => Result) =
    WithAuthentication(b) { employee => request =>
      if (employee.hasRole(role) && checkAccess(request, List(role), employee))
        f(employee)(request)
      else
        onUnauthorized(request)
    }

  def checkAccess(request: Request[JsValue], roles: List[String], employee: Employee) = {
    val id = (request.body \ "id").asOpt[String]
    var parentId = (request.body \ "parentId").asOpt[String]

    val accessRoles = roles.map(role => role match {
      case "PodLead"  => isSamePOD(id, parentId, employee)
      case "PodKeeper"  => isSamePOD(id, parentId, employee)
      case "User" => (id.isDefined && (id == employee.id))
      case "Admin" => true
    }
    )
    accessRoles.find(_ == true).isDefined
  }
  def isSamePOD(id: Option[String], parentId: Option[String], employee: Employee) = {
    if (!(id.isDefined ||parentId.isDefined)) false
    else {
      val parent = if (!parentId.isDefined) {
         employeeService.getEmployeeById(id.get).map(_.parent)
      } else parentId
      parent.isDefined && (parent == employee.parent)
    }
  }
  def HasAllRoles(roles: String*)(b: BodyParser[JsValue])(f: => Employee => Request[JsValue] => Result) =
    WithAuthentication(b) { employee => request =>
      if (employee.hasAllRoles(roles:_*))
        f(employee)(request)
      else
        onUnauthorized(request)
    }

  def HasAnyRole(roles: String*)(b: BodyParser[JsValue])(f: => Employee => Request[JsValue] => Result) =
    WithAuthentication(b) { employee => request =>
      if (employee.hasAnyRole(roles:_*) &&  checkAccess(request, roles.toList, employee))
        f(employee)(request)
      else
        onUnauthorized(request)
    }
}