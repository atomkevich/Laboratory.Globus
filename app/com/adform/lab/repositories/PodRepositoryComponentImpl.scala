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
        override def getAncestorsById(id: String): Option[List[String]] = {
           MongoContext.podCollection.findOne(MongoDBObject("_id" -> id)).map(pod => pod.get("ancestors").asInstanceOf[BasicDBList])
            .map(ancestors =>  Helper.fromBasicDBListToList(ancestors))
        }

        override def save(pod: POD): Unit = {
          MongoContext.podCollection.save(PODConverter.toBson(pod))
        }

        override def getById(id: String): Option[POD] = {
           MongoContext.podCollection.findOne(MongoDBObject("_id" -> id)).map(item => PODConverter.fromBson(item))
        }

        override def find(params: Map[String, String], skip: Int, limit: Int): List[POD] = {
           MongoContext.podCollection.find(params).skip(skip).limit(limit).map(pod => PODConverter.fromBson(pod)).toList
        }

        override def updateProfile(podId: String, profileAttribute: Map[String, String]): Unit = {
          MongoContext.podCollection.update(MongoDBObject("_id"-> podId), $set("profile" -> profileAttribute))
        }

        override def movePOD(id: String, parentId: String, ancestors: List[String]): Unit = {
          MongoContext.podCollection.update(MongoDBObject("_id" -> id), $set("parentId" -> parentId, "ancestors" -> ancestors))
        }

        override def getChildsById(id: String): List[POD] = {
          MongoContext.podCollection.find(MongoDBObject("parentId" -> id)).map(pod => PODConverter.fromBson(pod)).toList
        }

        override def deletePODs(ids: List[String]): Unit = {
          MongoContext.podCollection.remove("_id" $in ids)
        }
      }
}
