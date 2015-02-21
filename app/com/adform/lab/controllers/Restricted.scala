package com.adform.lab.controllers

/**
 * Created by Alina_Tamkevich on 2/16/2015.
 */

import com.adform.lab.repositories.{PodRepositoryComponentImpl, EmployeeRepositoryComponentImpl}
import com.adform.lab.services.{EmployeeProfileServiceComponentImpl, EmployeeServiceComponentImpl, PODServiceComponentImpl}
import play.api.mvc.Controller
import views.html

object Restricted extends Controller  with Secured {


  /*def loggedUserArea = WithAuthentication { employee => implicit request =>
    Ok(html.restricted(employee, employee.employeeProfile.email))
  }


  def adminArea = HasRole("PODKeeper") { employee => implicit request =>
    Ok(html.restricted(employee, employee.employeeProfile.email))
  }

  def adminArea2 = HasRole("PODKeeper") { employee => implicit request =>
    Ok(html.restricted(employee, employee.employeeProfile.email))
  }*/
}