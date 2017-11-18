package services.communication

import javax.inject.Inject

import akka.actor.{ActorSystem, Props}
import models.{NotifyAutomaticAdjustmentModel, NotifyNodeSoundChangeModel}
import services.node.NodeService

class UDPCommunicationService @Inject()(nodeService: NodeService) extends CommunicationService {
  val system = ActorSystem("UdpCommunicationSystem")
  val udpSender = system.actorOf(Props[UDPSendingActor], "UdpSender")
  val persistenceActor = system.actorOf(PersistenceActor.props(nodeService), "PersistenceActor")
  val udpReceiver = system.actorOf(UDPReceivingActor.props(persistenceActor), "UdpReceiver")
  val encodingActor = system.actorOf(EncodingActor.props(udpSender))

   def notifyNodeSoundChange(notifyNodeSoundChangeModel: NotifyNodeSoundChangeModel): Unit = {
     encodingActor ! notifyNodeSoundChangeModel
   }

   def notifyAutomaticAdjustment(notifyAutomaticAdjustmentModel: NotifyAutomaticAdjustmentModel): Unit = {
     encodingActor ! notifyAutomaticAdjustmentModel
   }
}
