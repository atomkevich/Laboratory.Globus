package com.adform.lab.controllers

/**
 * Created by Alina_Tamkevich on 2/16/2015.
 */

import com.adform.lab.domain.Employee
import play.api.Play.current
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views._
import play.api.cache.Cache

object Authentication extends Controller with Secured{

    val loginForm = Form(
      tuple(
        "email" -> text,
        "password" -> text
      ) verifying ("Invalid email or password", result => result match {
        case (email, password) => Application.getEmployeeByEMail(email).isDefined
      })
    )


    def login = Action { implicit request =>
      Ok(html.login(loginForm))
    }


    def logout = WithAuthentication { employee => _ =>
      Cache.remove(employee.employeeProfile.email)
      Redirect(routes.Application.index).withNewSession.flashing("success" -> "You've been logged out")
    }


    def authenticate = Action { implicit request =>
      val loginError = Redirect(routes.Authentication.login).flashing("message" -> "Login or password incorrect")
      loginForm.bindFromRequest.fold(
        formWithErrors => loginError,
        form => {
          Application.getEmployeeByEMail(form._1) match {
            case Some(employee: Employee) => {
              Cache.set(form._1, employee, 10000)
              Redirect(routes.Application.index).withSession("email" -> form._1)
            }
            case None => loginError
          }
        }
      )
    }
}