package com.adform.lab.controllers.spray

import com.adform.lab.converters.Helper
import com.adform.lab.domain.Employee
import com.adform.lab.services.EmployeeServiceComponent
import spray.routing.{AuthenticationFailedRejection, RequestContext}
import spray.routing.authentication._
import spray.routing.directives.AuthMagnet
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by atamkevich on 08/03/15.
 */
trait Authenticator extends EmployeeServiceComponent {
  def userAuthorization(roles: String*)(implicit ec: ExecutionContext): AuthMagnet[Employee] = {
    def validateUser(userPass: Option[UserPass]): Option[Employee] = {
      for {
        p <- userPass
        employee <- employeeService.findEmployeeByEmail(p.user)
        if employee.passwordMatches(p.pass) && employee.hasAnyRole(roles:_*)
      } yield employee
    }

    def authenticator(userPass: Option[UserPass]): Future[Option[Employee]] = Future { validateUser(userPass) }

    BasicAuth(authenticator _, realm = "Private API")
  }
}