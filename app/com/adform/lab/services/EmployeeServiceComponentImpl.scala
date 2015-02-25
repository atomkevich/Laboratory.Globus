package com.adform.lab.services

import com.adform.lab.config.MongoContext
import com.adform.lab.converters.Helper
import com.adform.lab.domain._
import org.bson.types.ObjectId
import com.adform.lab.repositories.EmployeeRepositoryComponent

/**
 * Created by HP on 08.02.2015.
 */
trait EmployeeServiceComponentImpl extends EmployeeServiceComponent {

  this: EmployeeRepositoryComponent
    with EmployeeProfileServiceComponent
    with PODServiceComponent =>

  override def employeeService: EmployeeService = new EmployeeServiceImpl


  class EmployeeServiceImpl extends EmployeeService {


    override def createNewEmployee(email: String, roles: List[String], parentId: Option[String]): Unit = {
      if (parentId.isDefined) validateRole(roles, parentId.get)
      val employeeProfile = employeeProfileService.getEmployeeProfileByEmail(email)
      val ancestors: List[String] = parentId.map(id =>
        podService.getAncestorsById(parentId.get).map(_ :+ id).getOrElse(List())
      ).getOrElse(List())
      val employeeRoles = Helper.convertToRoles(roles)
      employeeRepository.save(Employee(Helper.generateId, employeeProfile, parentId.getOrElse(null), employeeRoles, ancestors))
    }

    override def getAllEmployees(params: Map[String, String]): List[Employee] = {
      val page = Helper.getPagination(params)._1
      val size = Helper.getPagination(params)._2
      employeeRepository.find(Helper.createSearchQuery(params), (page-1)*size, page*size)
    }

    override def getEmployeeById(id: String): Option[Employee] = {
      employeeRepository.get(id)
    }

    def validateRole(roles: List[String], parentId : String) ={
      if (!employeeRepository.getEmployeesByRoleAndPod(roles.filter(role => ("PODKeeper".equals(role) || "PODLead".equals(role))), parentId).isEmpty) {
        throw new IllegalArgumentException("Pod " + parentId + "already has PODKeeper or PODLead")
      }
    }

    override def assignRoles(id: String, roles: List[String]): Unit = {
      val employee = employeeRepository.get(id)
      validateRole(roles, employee.get.parent)
      val employeeRoles = Helper.convertToRoles(roles)
      employeeRepository.assignRole(id, roles)
    }

    override def updateProfile(id: String, fields: Map[String, String]): Unit = {
      val employee = employeeRepository.get(id)
     if (!employee.isDefined) throw new IllegalArgumentException("employee " + id + "is not present ")
      val profile = employee.get.employeeProfile
      val default = Map("name" -> profile.name, "location" -> profile.location) ++ Helper.defaultEmployeeProfileAttrubute(fields)
      val custom = profile.customAttribute ++ Helper.getCustomAttribute(fields)
      val result: Map[String, Any] = default ++ Map("custom_attribute" -> custom)
      employeeRepository.updateProfile(id, result)
    }

    override def deleteEmployees(ids: List[String]): Unit = {
      employeeRepository.deleteEmployees(ids)
    }

    override def multiUpdate(update: (String, String)): Unit = {
      employeeRepository.multiUpdate(update)
    }

    override def findEmployeeByEmail(email: String): Option[Employee] = employeeRepository.getByEmail(email)
  }
}
