package com.adform.lab.repositories

import com.adform.lab.config.MongoContext
import com.adform.lab.converters.EmployeeConverter
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.{MongoDB, MongoConnection}
import com.mongodb.casbah.commons.ValidBSONType.BasicDBObject
import com.adform.lab.domain._

import scala.collection.JavaConverters._

/**
 * Created by HP on 08.02.2015.
 */
trait EmployeeRepositoryComponentImpl extends  EmployeeRepositoryComponent{
  def employeeRepository = new EmployeeRepositoryImpl

  class EmployeeRepositoryImpl extends EmployeeRepository {
    override def get(id: String): Option[Employee] = {
      MongoContext.employeeCollection.findOne(MongoDBObject("_id" -> id)).map(employee => EmployeeConverter.fromBson(employee))
    }

    override def delete(id: String): Unit = {
      MongoContext.employeeCollection.remove(MongoDBObject("_id" -> id))
    }

    override def save(employee: Employee): Unit = {
      val document = EmployeeConverter.toBson(employee)
      MongoContext.employeeCollection.save(document)
    }

    override def find(search: Map[String, String], skip: Int, limit: Int): List[Employee] = {
      val employees = MongoContext.employeeCollection.find(search).skip(skip).limit(limit)
      (for (employee <- employees) yield EmployeeConverter.fromBson(employee)).toList
    }

    override def getEmployeesByRoleAndPod(role: List[String], parentId: String): List[Employee] = {
      val searchQuery: DBObject = ("role" $in role) ++ ("parentId" -> parentId)
      val employees = MongoContext.employeeCollection.find(searchQuery)
      employees.map(employee => EmployeeConverter.fromBson(employee)).toList
    }

    override def assignRole(id: String, role: List[String]): Unit = {
      MongoContext.employeeCollection.update(MongoDBObject("_id" -> id), $set("role" -> role))
    }

    override def updateProfile(id: String, result: Map[String, Any]): Unit = {
      MongoContext.employeeCollection.update(MongoDBObject("_id" -> id),  $set("profile" -> result))
    }

    override def deleteEmployees(ids: List[String]): Unit = {
      MongoContext.employeeCollection.remove("_id" $in ids)
    }

    override def multiUpdate(updateQuery: (String, String)): Unit = {
      MongoContext.employeeCollection.update(MongoDBObject.empty, $set(updateQuery), multi = true)
    }

    override def getByEmail(email: String): Option[Employee] = {
      MongoContext.employeeCollection.findOne(MongoDBObject("profile.email" -> email.toLowerCase))
        .map(employee => EmployeeConverter.fromBson(employee))
    }
  }
}
