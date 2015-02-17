package com.adform.lab.domain

import scala.collection.mutable

/**
 * Created by Alina_Tamkevich on 2/9/2015.
 */
case class EmployeeProfile(var name: String, var email: String, var location: String) {
  var  customAttribute: Map[String, String]= Map()
    def addProfileAttribute(key: String, value: String) = {
      customAttribute += (key ->  value)
    }

    def addAllProfileAttribute(attribute: Map[String, String]) = {
      customAttribute = attribute
    }
    def deleteProfileAttribute(key: String) = {
      customAttribute -= key
    }

}
