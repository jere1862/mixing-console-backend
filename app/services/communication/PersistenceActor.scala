package services.communication

import akka.actor.{Actor, Props}
import models.DataModel
import services.node.NodeService

class PersistenceActor(nodeService: NodeService) extends Actor{
  def receive = {
    case data: DataModel =>
      println("Received data in persistence actor")
      println(data)
  }
}

object PersistenceActor{
  def props(nodeService: NodeService) = Props(new PersistenceActor(nodeService))
}
