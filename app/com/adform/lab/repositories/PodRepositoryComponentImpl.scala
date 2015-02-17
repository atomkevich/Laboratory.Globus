package com.adform.lab.repositories


import com.adform.lab.config.MongoContext
import com.adform.lab.converters.{Helper, PODConverter}
import com.mongodb.casbah.Imports._
import com.mongodb.{BasicDBList}
import com.adform.lab.domain.POD

/**
 * Created by Alina_Tamkevich on 2/11/2015.
 */
trait PodRepositoryComponentImpl extends PodRepositoryComponent{
    def podRepository = new PodRepositoryImpl

      class PodRepositoryImpl extends  PodRepository {
        override def getAncestorsById(id: String): List[String] = {
          val ancestors = MongoContext.podCollection.findOne(MongoDBObject("_id" -> id)).get.get("ancestors").asInstanceOf[BasicDBList]
          Helper.fromBasicDBListToList(ancestors)
        }

        override def save(pod: POD): Unit = {
          MongoContext.podCollection.save(PODConverter.toBson(pod))
        }

        override def getById(id: String): Option[POD] = {
          val pod = MongoContext.podCollection.findOne(MongoDBObject("_id" -> id))
          Option(PODConverter.fromBson(pod.get))
        }

        override def find(params: Map[String, String], skip: Int, limit: Int): List[POD] = {
          val pods = MongoContext.podCollection.find(params).skip(skip).limit(limit)
          (for (pod <- pods) yield PODConverter.fromBson(pod)).toList
        }

        override def updateProfile(podId: String, profileAttribute: Map[String, String]): Unit = {
          MongoContext.podCollection.update(MongoDBObject("_id"-> podId), $set("profile" -> profileAttribute))
        }
      }
}
