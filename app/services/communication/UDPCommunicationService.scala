package services.communication

import java.net.InetSocketAddress
import javax.inject.Inject

import akka.actor.ActorSystem
import models.{NotifyAutomaticAdjustmentModel, NotifyNodeSoundChangeModel, NotifySoundLimitedModel}
import com.typesafe.config.Config
import services.node.NodeService

class UDPCommunicationService @Inject()(nodeService: NodeService, configuration: Config) extends CommunicationService {
  val destinationHostname: String = configuration.getString("fixnode.hostname")
  val destinationPort: Int = configuration.getInt("fixnode.port")
  val receivingPort: Int = configuration.getInt("receiving.port")

  val destinationAddress = new InetSocketAddress(destinationHostname, destinationPort)
  val receivingAddress = new InetSocketAddress("localhost", receivingPort)

  val system = ActorSystem("UdpCommunicationSystem")
  val udpSender = system.actorOf(UDPSendingActor.props(destinationAddress), "UdpSender")
  val persistenceActor = system.actorOf(PersistenceActor.props(nodeService), "PersistenceActor")
  val udpReceiver = system.actorOf(UDPReceivingActor.props(persistenceActor, receivingAddress), "UdpReceiver")
  val encodingActor = system.actorOf(EncodingActor.props(udpSender))

   def notifyNodeSoundChange(notifyNodeSoundChangeModel: NotifyNodeSoundChangeModel): Unit = {
     encodingActor ! notifyNodeSoundChangeModel
   }

   def notifyAutomaticAdjustment(notifyAutomaticAdjustmentModel: NotifyAutomaticAdjustmentModel): Unit = {
     encodingActor ! notifyAutomaticAdjustmentModel
   }

  def notifySoundLimited(notifySoundLimitedModel: NotifySoundLimitedModel) : Unit = {
     encodingActor ! notifySoundLimitedModel
  }
}
