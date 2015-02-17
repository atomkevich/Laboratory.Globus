package com.adform.lab.domain

/**
 * Created by Alina_Tamkevich on 2/13/2015.
 */

object EmployeeFilterField extends Enumeration {
  type EmployeeFilterField = Value
  val LOCATION, NAME, ROLE, EMAIL = Value


  def containsKey(key: String): Boolean = {
    values.find(key.toUpperCase == _.toString).isDefined
  }
}