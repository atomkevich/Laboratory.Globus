package com.adform.lab.controllers.spray


import akka.actor.Actor
import spray.routing._


class HttpServiceActor extends Actor with ServiceApi {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(route)
}

 trait ServiceApi extends HttpService with ApplicationContext
        with EmployeesApi with PODApi{

  implicit def executionContext = actorRefFactory.dispatcher
  val route =  podRoute ~ employeeRoute

}