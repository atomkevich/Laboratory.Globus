package com.adform.lab.domain

import com.adform.lab.converters.Helper

/**
 * Created by Alina_Tamkevich on 2/11/2015.
 */
case class Employee(id: Option[String],
                    var employeeProfile : EmployeeProfile,
                    var parent: String,
                    var roles : List[Role],
                     var ancestors: List[String]) {
  def hasRole(role: String) = Helper.convertRolesToString(this.roles).contains(role)

  def hasAnyRole(roles: String*) =
    !Helper.convertRolesToString(this.roles).intersect(roles).isEmpty

  def hasAllRoles(roles: String*) =
    Helper.convertRolesToString(this.roles) forall (this.roles.contains)
}