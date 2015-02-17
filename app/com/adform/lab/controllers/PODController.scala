package com.adform.lab.controllers

import com.adform.lab.converters.Helper
import com.adform.lab.domain.{POD, Employee}
import play.api.libs.json.{JsObject, JsValue, Writes, Json}
import play.api.mvc._
import com.adform.lab.services.PODServiceComponent

/**
 * Created by Alina_Tamkevich on 2/11/2015.
 */
trait PODController extends Controller with Secured{

  this: PODServiceComponent =>

  implicit val podWrites = new Writes[POD] {
    override def writes(pod: POD): JsValue = {
      Json.obj(
        "id" -> pod.id,
        "name" -> pod.podProfile.name,
        "location" -> pod.podProfile.location,
        "parentId" -> pod.parent,
        "ancestors" -> pod.ancestors
      )
    }
  }


  def createPOD = Action(parse.json) { request =>
    val name = (request.body \ "name").asOpt[String]
    val location = (request.body \ "location").asOpt[String].getOrElse("")
    val description = (request.body \ "description").asOpt[String].getOrElse("")
    val parentId = (request.body \ "parentId").asOpt[String]

    if (name.isDefined) {
      podService.createPOD(parentId, name.get, location, description)
      Created
    } else {
      BadRequest("Missing params")
    }
  }

  def findPODs() = HasRole("PODKeeper"){employee => implicit request =>
    val params = request.queryString.map { case (k, v) => k -> v.mkString}
    var pods = podService.getPODs(params)
    if (!pods.isEmpty) {
      Ok(Json.toJson(pods))
    } else {
      NotFound
    }
  }

  def getPODById(id: String) = Action {
    val pod: Option[POD] = podService.getPODById(id)
    if (pod.isDefined) {
      Ok(Json.toJson(pod.get))
    } else {
      NotFound
    }
  }

  def updatePODProfile = Action(parse.json) { request =>
    val podId = (request.body\ "id").asOpt[String]
    val fields = request.body.asInstanceOf[JsObject].value.map { case (k, v) => k -> v.as[String]}.filter(t => !"id".equals(t._1))
    if (podId.isDefined) {
      podService.updateProfile(podId.get, fields.toMap)
      Ok("Successfully updated")
    } else {
      BadRequest("Wrong params")
    }
  }
}
