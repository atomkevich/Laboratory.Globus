package com.adform.lab.controllers

/**
 * Created by Alina_Tamkevich on 2/16/2015.
 */

import com.adform.lab.controllers.Authentication._
import com.adform.lab.converters.Helper
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
      if (employee.hasAnyRole(role) && Helper.checkAccess(request, List(role), employee))
        f(employee)(request)
      else
        onUnauthorized(request)
    }


  def HasAllRoles(roles: String*)(b: BodyParser[JsValue])(f: => Employee => Request[JsValue] => Result) =
    WithAuthentication(b) { employee => request =>
      if (employee.hasAnyRole(roles:_*))
        f(employee)(request)
      else
        onUnauthorized(request)
    }

  def HasAnyRole(roles: String*)(b: BodyParser[JsValue])(f: => Employee => Request[JsValue] => Result) =
    WithAuthentication(b) { employee => request =>
      if (employee.hasAnyRole(roles:_*) &&  Helper.checkAccess(request, roles.toList, employee))
        f(employee)(request)
      else
        onUnauthorized(request)
    }
}