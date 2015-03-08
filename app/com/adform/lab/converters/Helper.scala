package com.adform.lab.converters

import com.adform.lab.controllers.Authentication._
import com.mongodb.BasicDBList
import com.adform.lab.domain._
import com.mongodb.casbah.commons.MongoDBList
import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import play.api.mvc.Request

/**
 * Created by Alina_Tamkevich on 2/11/2015.
 */
object Helper {
  val convertParam: Map[String, String] =
    Map(
    "name" -> "profile.name",
    "email" -> "profile.email",
    "location" -> "profile.location",
    "description" -> "profile.description"
  )


  def convertToRoles(roles: List[String]): List[Role] = {
    roles match  {
      case List() => List(Viewer)
      case rolesList =>  rolesList map {
        case "Admin" => AdminRole
        case "PODLead" => PODLeadRole
        case "PODKeeper" => PODKeeperRole
        case roleName: String => CustomRole(roleName)
        case _ => Viewer
      }
    }
  }


  def generateId = Option(ObjectId.get().toString)

  def fromBasicDBListToList(dbList: MongoDBList):List[String] = {
    if (Option(dbList).isDefined)
       dbList.map(_.toString).toList else null
  }

  def createSearchQuery(params: Map[String, String]): Map[String, String] = {
    val validateParams = defaultEmployeeProfileAttrubute(params)
    validateParams.map({case (k, v) => (convertParam.get(k).getOrElse(k), v)})
  }

  def defaultEmployeeProfileAttrubute(params: Map[String, String]): Map[String, String] = {
    params.filter({case (x, y) => (EmployeeFilterField.containsKey(x))})
  }
  def getPagination(params:Map[String, String]) = {
    val pageNumber = params.get("page").getOrElse(1).asInstanceOf[Int]
    val pageSize = params.get("size").getOrElse(10).asInstanceOf[Int]
    (pageNumber, pageSize)
  }

  def getCustomAttribute(params: Map[String, String]) ={
    params.filter({case(x, y) => !EmployeeFilterField.containsKey(x)})
  }

  def checkAccess(request: Request[JsValue], roles: List[String], employee: Employee) = {
    val id = (request.body \ "id").asOpt[String]
    val parentId = (request.body \ "parentId").asOpt[String]

    val accessRoles = roles.map(role => role match {
      case "PODLead"  => isSamePOD(id, parentId, employee)
      case "PODKeeper"  => isSamePOD(id, parentId, employee)
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

}
