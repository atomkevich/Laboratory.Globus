package com.adform.lab.services

import com.adform.lab.domain.EmployeeProfile
import net.liftweb.json._

import scalaj.http.Http

/**
 * Created by Alina_Tamkevich on 2/9/2015.
 */
trait EmployeeProfileServiceComponentImpl extends EmployeeProfileServiceComponent {
  override def employeeProfileService: EmployeeProfileService = new EmployeeYammerProfileService

  class EmployeeYammerProfileService extends EmployeeProfileService {
    val getProfileByEmailUrl = "https://www.yammer.com/api/v1/users/by_email.json"


    override def getEmployeeProfileByEmail(email: String): EmployeeProfile = {
      val request = Http(getProfileByEmailUrl).param("email", email)
        .header("Authorization", "Bearer wTVEERLKQaF8OzJ2Rxax6A")
        .header("Host", "www.yammer.com")
        .header("Content-Type", "application/json")

      implicit val formats = DefaultFormats
      val parsed = parse(request.asString.body).toOpt.map(body => EmployeeProfile(
        (body \ "name").values.asInstanceOf[String],
        (body \ "email").values.asInstanceOf[String],
        (body \ "location").values.asInstanceOf[String],
        (body \ "web_url").values.asInstanceOf[String],
        null
      ))
      parsed.getOrElse(EmployeeProfile(null, email, null, null, null))
    }
  }

}

