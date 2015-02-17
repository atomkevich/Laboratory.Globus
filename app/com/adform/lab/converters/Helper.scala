package com.adform.lab.converters

import com.mongodb.BasicDBList
import com.adform.lab.domain._
import org.bson.types.ObjectId

/**
 * Created by Alina_Tamkevich on 2/11/2015.
 */
object Helper {
  val convertParam: Map[String, String] =
    Map(
    "name" -> "profile.name",
    "location" -> "profile.location",
    "description" -> "profile.description"
  )


  def convertToRoles(roles: List[String]): List[Role] = {
      if (!Option(roles).isDefined || roles.isEmpty) {
        List(Viewer)
      } else {
        roles map {
          case "Admin" => AdminRole
          case "PODLead" => PODLeadRole
          case "PODKeeper" => PODKeeperRole
          case roleName: String => CustomRole(roleName)
          case _ => Viewer
        }
      }
    }

  def convertRolesToString(roles: List[Role]): List[String] = {
    if (!Option(roles).isDefined || roles.isEmpty) {
      List("Viewer")
    } else {
      roles map {
        case AdminRole => "Admin"
        case PODLeadRole => "PODLead"
        case PODKeeperRole => "PODKeeper"
        case  CustomRole(roleName) => roleName
        case _ => "Viewer"
      }
    }
  }

  def generateId = Option(ObjectId.get().toString)

  def fromBasicDBListToList(dbList: BasicDBList):List[String] = {
    List(dbList.toArray()).flatten.asInstanceOf[List[String]]
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
}
