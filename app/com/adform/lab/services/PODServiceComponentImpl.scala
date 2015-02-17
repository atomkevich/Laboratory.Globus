package com.adform.lab.services

import com.adform.lab.converters.Helper
import com.adform.lab.domain.{POD, PODProfile}
import com.adform.lab.repositories.PodRepositoryComponent

/**
 * Created by Alina_Tamkevich on 2/11/2015.
 */
trait PODServiceComponentImpl extends PODServiceComponent{
  this: PodRepositoryComponent =>
  override def podService: PODService = new PODServiceImpl

  class PODServiceImpl extends PODService {
    override def getAncestorsById(id: String): List[String] = podRepository.getAncestorsById(id)

    override def createPOD(parentId: Option[String], name: String, location: String, description: String): Unit = {
      val podProfile = PODProfile(name, location, description)
      val ancestors = if (parentId.isDefined) podRepository.getAncestorsById(parentId.get) :+ parentId.get else List()
      podRepository.save(POD(Helper.generateId, podProfile, ancestors, parentId.getOrElse(null)))
    }

    override def getPODById(id: String): Option[POD] = podRepository.getById(id)

    override def getPODs(params: Map[String, String]): List[POD] = {
      val page = Helper.getPagination(params)._1
      val size = Helper.getPagination(params)._2
      podRepository.find(Helper.createSearchQuery(params), (page-1)*size, page*size)
    };

    override def updateProfile(podId: String, params: Map[String, String]): Unit = {
      val pod = podRepository.getById(podId)
      if (!pod.isDefined) throw new IllegalArgumentException("pod " + podId + "is not present")
      val profile = pod.get.podProfile
      val updateProfile = Map("name" -> profile.name, "location" -> profile.location, "description" -> profile.description) ++ params
      podRepository.updateProfile(podId, updateProfile)
    }
  }
}
