package com.adform.lab.config

import java.util.Properties
import com.mongodb.casbah._
import com.mongodb.ServerAddress
import play.Play

/**
 * Created by Alina_Tamkevich on 2/10/2015.
 */

object MongoContext {
  //val config = Play.application().configuration()

  val podDB = MongoConnection("EPBYMINW1130")("POD")

  val employeeCollection = podDB("employees")
  val podCollection = podDB("pod")
}