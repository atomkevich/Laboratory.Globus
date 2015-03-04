package com.adform.lab.domain

import com.adform.lab.converters.Helper

/**
 * Created by Alina_Tamkevich on 2/11/2015.
 */
case class Employee(id: Option[String],
                    password: String,
                    employeeProfile : EmployeeProfile,
                    parent: String,
                    roles : List[Role],
                    ancestors: List[String]) {

  def hasRole(role: String) = this.roles.map(_.name).contains(role)

  def hasAnyRole(roles: String*) = !this.roles.map(_.name).intersect(roles).isEmpty

  def hasAllRoles(roles: String*) = this.roles.map(_.name) forall (this.roles.contains)
}