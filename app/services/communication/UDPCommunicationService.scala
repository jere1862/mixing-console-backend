package services.communication

import javax.inject.Inject

import akka.actor.{ActorSystem, Props}
import services.node.NodeService

class UDPCommunicationService @Inject()(nodeService: NodeService) extends CommunicationService {
  val system = ActorSystem("UdpCommunicationSystem")
  val udpSender = system.actorOf(Props[UDPSendingActor], "UdpSender")
  val persistenceActor = system.actorOf(PersistenceActor.props(nodeService), "PersistenceActor")
  val udpReceiver = system.actorOf(UDPReceivingActor.props(persistenceActor), "UdpReceiver")

  override def send(message: String): Unit = {

  }
}
