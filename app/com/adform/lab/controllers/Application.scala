package com.adform.lab.controllers

import play.api.mvc._
import com.adform.lab.repositories.{PodRepositoryComponentImpl, EmployeeRepositoryComponentImpl}
import com.adform.lab.services.{PODServiceComponentImpl, EmployeeProfileServiceComponentImpl, EmployeeServiceComponentImpl}

object Application extends EmployeeController
                          with PODController
                          with EmployeeServiceComponentImpl
                          with EmployeeProfileServiceComponentImpl
                          with EmployeeRepositoryComponentImpl
                          with PODServiceComponentImpl
                          with PodRepositoryComponentImpl
{
  def index = Action {
    Ok(views.html.index("POD Structure"))
  }


}