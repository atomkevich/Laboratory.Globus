package com.adform.lab.controllers.spray

import com.adform.lab.controllers.Secured
import com.adform.lab.repositories.{EmployeeRepositoryComponentImpl, PodRepositoryComponentImpl}
import com.adform.lab.services.{PODServiceComponentImpl, EmployeeProfileServiceComponentImpl, EmployeeServiceComponentImpl, EmployeeServiceComponent}

/**
 * Created by v.bazarevsky on 3/1/15.
 */
trait ApplicationContext extends  EmployeeServiceComponentImpl
                            with EmployeeProfileServiceComponentImpl
                            with EmployeeRepositoryComponentImpl
                            with PODServiceComponentImpl
                            with PodRepositoryComponentImpl with Secured{
}