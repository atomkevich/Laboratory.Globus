package com.adform.lab.domain

/**
 * Created by HP on 07.02.2015.
 */
sealed  trait Role

case object AdminRole extends Role
case object PODLeadRole extends Role
case object PODKeeperRole extends Role
case object Viewer extends Role

case class CustomRole(name: String) extends Role

