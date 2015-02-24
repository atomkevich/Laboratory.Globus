package com.adform.lab.services

import com.adform.lab.domain.POD

/**
 * Created by Alina_Tamkevich on 2/11/2015.
 */
trait PODServiceComponent {
  def podService: PODService


  trait PODService {
    def getAncestorsById(id: String): Option[List[String]]
    def getPODById(id: String): Option[POD]
    def getPODs(params : Map[String, String]): List[POD]
    def createPOD(parentId: Option[String], name: String, location: String, description: String)
    def updateProfile(podId:String, params:Map[String, String])
    def linkPOD(firstPodId: String, secondPodId: String) : Option[POD]
    def getPODChildsById(id: String): List[POD]
    def getPODLinksById(id: String): List[POD]
    def getParentPOD(id: String): Option[POD]
  }
}
