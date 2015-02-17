package com.adform.lab.converters

import com.mongodb.{BasicDBList, BasicDBObject, DBObject}
import com.mongodb.casbah.commons.MongoDBObject
import com.adform.lab.domain.{EmployeeProfile, Role, Employee}

import scala.collection.mutable

/**
 * Created by Alina_Tamkevich on 2/10/2015.
 */
object EmployeeConverter {


    def toBson(employee: Employee): DBObject = {
      MongoDBObject(
        "_id" -> employee.id,
        "parent_id" -> employee.parent,
        "ancestors" -> employee.ancestors,
        "roles"  -> Helper.convertRolesToString(employee.roles),
        "profile" -> MongoDBObject(
            "name" -> employee.employeeProfile.name,
            "email"-> employee.employeeProfile.email,
            "location" -> employee.employeeProfile.location,
             "custom_attribute" -> employee.employeeProfile.customAttribute
        )
      )
    }

    def fromBson(document: DBObject): Employee = {
      val profileDocument = document.get("profile").asInstanceOf[BasicDBObject]
      val employeeProfile = EmployeeProfile(
        profileDocument.get("name").asInstanceOf[String],
        profileDocument.get("email").asInstanceOf[String],
        profileDocument.get("location").asInstanceOf[String]
      )
      Employee(
        Option(document.get("_id").asInstanceOf[String]),
        employeeProfile,
        document.get("parentId").asInstanceOf[String],
        Helper.convertToRoles(Helper.fromBasicDBListToList(document.get("roles").asInstanceOf[BasicDBList])),
        Helper.fromBasicDBListToList(document.get("ancestors").asInstanceOf[BasicDBList])
      )
    }
}
