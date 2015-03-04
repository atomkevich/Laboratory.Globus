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


    override def createNewEmployee(email: String, roles: List[String], parentId: Option[String]): Either[Unit, String] = {
      val  ancestors: Either[List[String], String] = parentId match {
        case Some(id) => {
          val validate = validateRole(roles, id)
           if (validate isRight) Right(validate.right.get)
           podService.getAncestorsById(id) match {
             case Some(ansList) => Left(ansList :+ id)
             case None => Left(List())
          }
        }
        case None => Left(List())
      }

      ancestors match {
        case Left(ancestors) =>  Left(employeeRepository.save(Employee(
          Helper.generateId,
          employeeProfileService.getEmployeeProfileByEmail(email),
          parentId.getOrElse(null),
          Helper.convertToRoles(roles),
          ancestors
        )))
        case Right(err) => Right(err)
      }

    }

    override def getAllEmployees(params: Map[String, String]): List[Employee] = {
      val page = Helper.getPagination(params)._1
      val size = Helper.getPagination(params)._2
      employeeRepository.find(Helper.createSearchQuery(params), (page-1)*size, page*size)
    }

    override def getEmployeeById(id: String): Option[Employee] = {
      employeeRepository.get(id)
    }

    def validateRole(roles: List[String], parentId : String): Either[Unit, String] = {
      employeeRepository
        .getEmployeesByRoleAndPod(roles.filter(role => ("PODKeeper".equals(role) || "PODLead".equals(role))), parentId) match {
        case List() => Right("Pod " + parentId + "already has PODKeeper or PODLead")
      }
    }

    override def assignRoles(id: String, roles: List[String]): Either[Unit, String] = {
       employeeRepository.get(id) match {
        case Some(employee) => {
          validateRole(roles, employee.parent) match {
            case Left(_) => Left(employeeRepository.assignRole(id, roles))
            case Right(err) => Right(err)
          }
        }
        case None => Right("Employee %s is not present".format(id))
      }
    }

    override def updateProfile(id: String, fields: Map[String, String]) : Either[String, String] = {
      employeeRepository.get(id) match {
        case Some(employee) => {
          val default = Map("name" -> employee.employeeProfile.name, "location" -> employee.employeeProfile.location) ++ Helper.defaultEmployeeProfileAttrubute(fields)
          val custom = employee.employeeProfile.customAttribute ++ Helper.getCustomAttribute(fields)
          val result: Map[String, Any] = default ++ Map("custom_attribute" -> custom)
          employeeRepository.updateProfile(id, result)
          Left("Success updated.")
        }
        case None => Right("Employee %s is not present".format(id))
      }
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
