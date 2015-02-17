package com.adform.lab.config

import java.util.Properties
import com.mongodb.casbah._
import com.mongodb.ServerAddress
import play.Play

/**
 * Created by Alina_Tamkevich on 2/10/2015.
 */

object MongoContext {
  val config = Play.application().configuration()

  val podDB = MongoConnection(config.getString("mongo.host"))(config.getString("mongo.dbName"))

  val employeeCollection = podDB("employees")
  val podCollection = podDB("pod")
}