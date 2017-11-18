package services.communication

import java.nio.ByteBuffer

import akka.actor.{Actor, ActorRef, Props}
import models.{NotifyAutomaticAdjustmentModel, NotifyNodeSoundChangeModel}

class EncodingActor(sendingActor: ActorRef) extends Actor {
  val NotifyNodeSoundChangeMessageLength: Int = 3;
  val NotifyAdjustAutomaticallyMessageLength: Int = 2;

  def receive = {
    case notifyNode:NotifyNodeSoundChangeModel =>
      sendingActor ! encodeSoundChangeModel(notifyNode)
    case adjustAutomatically: NotifyAutomaticAdjustmentModel =>
      sendingActor ! encodeAdjustAutomaticallyModel(adjustAutomatically)
    case _ =>
      println("UDPCommunicationService received wrong data type.")
  }

  def encodeSoundChangeModel(notifyNode: NotifyNodeSoundChangeModel) = {
    val byteBuffer = ByteBuffer.allocate(NotifyNodeSoundChangeMessageLength)
    byteBuffer.put(notifyNode.id.toByte)
    byteBuffer.put(notifyNode.sliderType.toByte)
    byteBuffer.put(notifyNode.value.toByte)
  }

  def encodeAdjustAutomaticallyModel(adjustAutomatically: NotifyAutomaticAdjustmentModel) = {
    val byteValue: Byte = if(adjustAutomatically.adjustAutomatically) 0x1 else 0x0
    val byteBuffer = ByteBuffer.allocate(NotifyAdjustAutomaticallyMessageLength)
    byteBuffer.put(adjustAutomatically.id.toByte)
    byteBuffer.put(byteValue)
  }
}

object EncodingActor {
  def props(sendingActor: ActorRef) = Props(new EncodingActor(sendingActor))
}
