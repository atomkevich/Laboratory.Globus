package com.adform.lab.controllers.spray

import akka.actor.Actor
import com.adform.lab.domain.POD
import spray.routing.HttpService


/**
 * Created by v.bazarevsky on 3/1/15.
 */
class HttpServiceActor extends Actor with Api {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(route)
}

sealed trait Api extends HttpService with ApplicationContext
        with EmployeesApi with PODApi{
  val route = employeeRoute ~ podRoute

}