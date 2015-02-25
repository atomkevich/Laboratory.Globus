package com.adform.lab.controllers

import java.io.File

import play.Play
import play.api.mvc._

object Application extends Controller with Secured{

  def index = WithAuthentication { employee => implicit request =>
    Ok(views.html.start())
  }
  def getURI(any: String): String = any match {

    case "employees" => "/public/html/employees/employees.html"
    case "employee" => "/public/html/employees/employee.html"
    case "newEmployee" => "/public/html/employees/newEmployee.html"

    case "pod" => "/public/html/pods/pod.html"
    case "pods" => "/public/html/pods/pods.html"
    case "newPOD" => "/public/html/pods/newPOD.html"

    case "admin" => "public/html/admin/admin.html"
    case _ => "error"
  }


  def loadPublicHTML(any: String) = Action {
    val projectRoot = Play.application().path()
    val file = new File(projectRoot + getURI(any))
    if (file.exists())
      Ok(scala.io.Source.fromFile(file.getCanonicalPath()).mkString).as("text/html");
    else
      NotFound
  }

}