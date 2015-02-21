package com.adform.lab.controllers

import java.io.File

import play.Play
import play.api.mvc._

object Application extends Controller with Secured{

 /* def index = Action {
    Ok(views.html.index("POD Structure"))
  }*/
  def index = WithAuthentication { employee => implicit request =>
    Ok(views.html.start())
  }
  def getURI(any: String): String = any match {
    case "main" => "/public/html/main.html"
    case "detail" => "/public/html/detail.html"
    case _ => "error"
  }

  /** load an HTML page from public/html */
  def loadPublicHTML(any: String) = Action {
    val projectRoot = Play.application().path()
    val file = new File(projectRoot + getURI(any))
    if (file.exists())
      Ok(scala.io.Source.fromFile(file.getCanonicalPath()).mkString).as("text/html");
    else
      NotFound
  }

}