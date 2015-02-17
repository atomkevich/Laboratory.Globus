package com.adform.lab.converters

import com.mongodb.{BasicDBList, BasicDBObject, DBObject}
import com.mongodb.casbah.commons.MongoDBObject
import com.adform.lab.domain.{PODProfile, POD, EmployeeProfile, Employee}

/**
 * Created by Alina_Tamkevich on 2/11/2015.
 */
object PODConverter {
  def toBson(pod: POD): DBObject = {
    MongoDBObject(
      "_id" -> pod.id,
      "parentId" -> pod.parent,
      "ancestors" -> pod.ancestors,
      "profile" -> MongoDBObject(
        "name" -> pod.podProfile.name,
        "location" -> pod.podProfile.location,
        "description" -> pod.podProfile.description
      )
    )
  }

  def fromBson(document: DBObject): POD = {
    val profileDocument = document.get("profile").asInstanceOf[BasicDBObject]
    val podProfile = PODProfile(
      profileDocument.get("name").asInstanceOf[String],
      profileDocument.get("location").asInstanceOf[String],
      profileDocument.get("description").asInstanceOf[String]
    )
    POD(
      Option(document.get("_id").asInstanceOf[String]),
      podProfile,
      Helper.fromBasicDBListToList(document.get("ancestors").asInstanceOf[BasicDBList]),
      document.get("parentId").asInstanceOf[String]
    )
  }
}
