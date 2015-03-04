package com.adform.lab.controllers

import com.adform.lab.controllers.Application._
import com.adform.lab.controllers.Authentication._
import com.adform.lab.controllers.EmployeeController._
import com.adform.lab.converters.Helper
import com.adform.lab.domain.{POD, Employee}
import com.adform.lab.repositories.PodRepositoryComponentImpl
import play.api.libs.json._
import play.api.mvc._
import com.adform.lab.services.{PODServiceComponentImpl, PODServiceComponent}

/**
 * Created by Alina_Tamkevich on 2/11/2015.
 */
object PODController extends Controller with Secured with PODServiceComponentImpl
with PodRepositoryComponentImpl{

  this: PODServiceComponent =>

  implicit val podWrites = new Writes[POD] {
    override def writes(pod: POD): JsValue = {
      Json.obj(
        "id" -> pod.id,
        "profile" -> Json.obj(
          "name" -> pod.podProfile.name,
          "location" -> pod.podProfile.location,
          "description" -> pod.podProfile.description
        ),
        "parentId" -> pod.parent,
        "ancestors" -> pod.ancestors
      )
    }
  }


  def createPOD = HasRole("Admin")(parse.json) { emplpyee => request =>
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

  def findPODs() = WithAuthentication {employee => request =>
    val params = request.queryString.map { case (k, v) => k -> v.mkString}
    val pods = podService.getPODs(params)
    Ok(Json.toJson(pods))
  }

  def linkPODs = HasRole("Admin") (parse.json) { employee => request =>
    val firstPodId = (request.body\ "firstPodId").asOpt[String]
    val secondPodId = (request.body\ "secondPodId").asOpt[String]
    if (!firstPodId.isDefined || !secondPodId.isDefined) {
      val parentPod = podService.linkPOD(firstPodId.get, secondPodId.get)
      parentPod match {
        case Left(pod) => Ok(Json.toJson(pod))
        case Right(err) => BadRequest(err)
      }

    } else {
      BadRequest("Wrong params")
    }
  }

  def getPODById(id: String) = WithAuthentication { employee => request =>
    val pod: Option[POD] = podService.getPODById(id)
    if (pod.isDefined) {
      Ok(Json.toJson(pod.get))
    } else {
      NotFound
    }
  }

  def delete(id: String) = HasAnyRole("PODKeeper", "PODLead", "User", "Admin")(parse.json) { employee => request =>
    if (!id.isEmpty) {
      podService.deletePODs(List(id))
      Ok("Successfully updated")
    } else {
      BadRequest("Wrong params")
    }
  }

  def updatePODProfile = HasAnyRole("Admin", "PODLead")(parse.json) { employee => request =>
    val podId = (request.body\ "id").asOpt[String]
    val fields = (request.body\ "profile").asInstanceOf[JsObject].value
      .map{case(k,v) => (k -> (if (v.isInstanceOf[JsString]) v.as[String] else null))}
    if (podId.isDefined) {
      podService.updateProfile(podId.get, fields.toMap)
      Ok("Successfully updated")
    } else {
      BadRequest("Wrong params")
    }
  }

  def getPODChilds(id: String) = WithAuthentication {employee => request =>
    val pods = podService.getPODChildsById(id)
    if (!pods.isEmpty) {
      Ok(Json.toJson(pods))
    } else {
      NotFound
    }
  }

  def getPODLinks(id: String) = WithAuthentication {employee => request =>
    val pods = podService.getPODLinksById(id)
    if (!pods.isEmpty) {
      Ok(Json.toJson(pods))
    } else {
      NotFound
    }
  }

  def getParentPOD(id: String) = WithAuthentication {employee => request =>
    val pod = podService.getParentPOD(id)
    if (pod.isDefined) {
      Ok(Json.toJson(pod))
    } else {
      NotFound
    }
  }

}
