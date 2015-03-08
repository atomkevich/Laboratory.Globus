import com.adform.lab.domain.{AdminRole, EmployeeProfile, Employee}
import com.adform.lab.repositories.{EmployeeRepositoryComponentImpl, EmployeeRepositoryComponent}
import org.mindrot.jbcrypt.BCrypt
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}

/**
 * Created by Alina_Tamkevich on 3/4/2015.
 */
class EmployeeRepositoryTest extends FlatSpec with Matchers  with BeforeAndAfter with MockitoSugar{
  var employeeRepositoryComponent:EmployeeRepositoryComponent =_
  var test: Employee = _
  before {
    employeeRepositoryComponent = new EmployeeRepositoryComponentImpl{}
    test = Employee(Some("1"), BCrypt.hashpw("test", BCrypt.gensalt()), EmployeeProfile("test", "test@gmail.com", "Minsk", "yammer_url", null), null, List(AdminRole), List())
  }

  "A employee repository" should "save entities" in {
    employeeRepositoryComponent.employeeRepository.save(test)
    val savedEmployee = employeeRepositoryComponent.employeeRepository.get(test.id.get)
    assert(savedEmployee.isDefined)
    savedEmployee.map(employee => {
      assert(employee.id equals test.id)
      assert(employee.employeeProfile.name equals test.employeeProfile.name)
    })
  }

  "A employee repository" should "find employees by search params" in {
    val employees = employeeRepositoryComponent.employeeRepository.find(Map("profile.location" -> "Minsk", "profile.name" -> "test"), 0, 10)
    assert(!employees.isEmpty)
    employees map (employee => {
      assert(employee.employeeProfile.name equals test.employeeProfile.name)
      assert(employee.employeeProfile.location equals test.employeeProfile.location)
    })
  }

  "A employee repository" should "update employee profile" in {
    employeeRepositoryComponent.employeeRepository.updateProfile("1", Map("profile.location" -> "Moskov", "profile.name" -> "new_name"))
    employeeRepositoryComponent.employeeRepository.get("1") map (employee => {
      assert(employee.employeeProfile.location equals test.employeeProfile.location)
      assert(employee.employeeProfile.name equals test.employeeProfile.name)
    })
  }

}
