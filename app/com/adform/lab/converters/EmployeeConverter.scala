package com.adform.lab.converters

import com.mongodb.{BasicDBList, BasicDBObject, DBObject}
import com.mongodb.casbah.commons.{MongoDBList, MongoDBObject}
import com.adform.lab.domain.{EmployeeProfile, Role, Employee}

import scala.collection.mutable

/**
 * Created by Alina_Tamkevich on 2/10/2015.
 */
object EmployeeConverter {


    def toBson(employee: Employee): DBObject = {
      MongoDBObject(
        "_id" -> employee.id,
        "parentId" -> employee.parent,
        "ancestors" -> employee.ancestors,
        "roles"  -> Helper.convertRolesToString(employee.roles),
        "profile" -> MongoDBObject(
            "name" -> employee.employeeProfile.name,
            "email"-> employee.employeeProfile.email,
            "location" -> employee.employeeProfile.location,
            "yammerUrl" -> employee.employeeProfile.yammerUrl,
            "custom_attribute" -> employee.employeeProfile.customAttribute
        )
      )
    }

    def fromBson(document: MongoDBObject): Employee = {
      val profileDocument = document.as[MongoDBObject]("profile")
      val employeeProfile = EmployeeProfile(
        profileDocument.as[String]("name"),
        profileDocument.as[String]("email"),
        profileDocument.as[String]("location"),
        profileDocument.as[String]("yammerUrl")
      )
      Employee(
        Option(document.as[String]("_id")),
        employeeProfile,
        document.as[String]("parentId"),
        Helper.convertToRoles(Helper.fromBasicDBListToList(document.as[MongoDBList]("roles"))),
        Helper.fromBasicDBListToList(document.as[MongoDBList]("ancestors"))
      )
    }
}
