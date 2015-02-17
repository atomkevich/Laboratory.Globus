package com.adform.lab.services

import com.adform.lab.domain.POD

/**
 * Created by Alina_Tamkevich on 2/11/2015.
 */
trait PODServiceComponent {
  def podService: PODService


  trait PODService {
    def getAncestorsById(id: String): List[String]
    def getPODById(id: String): Option[POD]
    def getPODs(params : Map[String, String]): List[POD]
    def createPOD(parentId: Option[String], name: String, location: String, description: String)
    def updateProfile(podId:String, params:Map[String, String])
  }
}
