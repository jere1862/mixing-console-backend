package services.communication

import akka.actor.{ActorRefFactory, ActorSystem, Props}

class UDPCommunicationService extends CommunicationService {
  val system = ActorSystem("UdpCommunicationSystem")
  val udpSender = system.actorOf(Props[UDPSendingActor], "UdpSender")
  val udpReceiver = system.actorOf(Props[UDPReceivingActor], "UdpReceiver")

  override def send(message: String): Unit = {

  }
}

