package services.communication

import java.nio.{ByteBuffer, ByteOrder}

import akka.actor.{Actor, ActorRef, Props}
import models.{NotifyAutomaticAdjustmentModel, NotifyNodeSoundChangeModel, NotifySoundLimitedModel}

class EncodingActor(sendingActor: ActorRef) extends Actor {
  val NotifyNodeSoundChangeMessageLength: Int = 3
  val NotifyAdjustAutomaticallyMessageLength: Int = 2
  val NotifySoundLimitedMessageLength: Int = 1

  def receive = {
    case notifyNode:NotifyNodeSoundChangeModel =>
      sendingActor ! encodeSoundChangeModel(notifyNode)
    case adjustAutomatically: NotifyAutomaticAdjustmentModel =>
      sendingActor ! encodeAdjustAutomaticallyModel(adjustAutomatically)
    case notifySoundLimitedModel: NotifySoundLimitedModel =>
      sendingActor ! encodeSoundLimitedModel(notifySoundLimitedModel)
    case _ =>
      println("UDPCommunicationService received wrong data type.")
  }

  def encodeSoundChangeModel(notifyNode: NotifyNodeSoundChangeModel) = {
    val byteBuffer = ByteBuffer.allocate(NotifyNodeSoundChangeMessageLength)
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
    byteBuffer.put(notifyNode.id.toByte)
    byteBuffer.put(notifyNode.sliderType.toByte)
    byteBuffer.put(notifyNode.value.toByte)
  }

  def encodeAdjustAutomaticallyModel(adjustAutomatically: NotifyAutomaticAdjustmentModel) = {
    val byteBuffer = ByteBuffer.allocate(NotifyAdjustAutomaticallyMessageLength)
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
    byteBuffer.put(adjustAutomatically.id.toByte)
    byteBuffer.put(booleanToByte(adjustAutomatically.adjustAutomatically))
  }

  def encodeSoundLimitedModel(notifySoundLimitedModel: NotifySoundLimitedModel) = {
    val byteBuffer = ByteBuffer.allocate(NotifySoundLimitedMessageLength)
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
    byteBuffer.put(booleanToByte(notifySoundLimitedModel.isSoundLimited))
  }

  private def booleanToByte(input: Boolean): Byte = if(input) 0x1 else 0x0
}

object EncodingActor {
  def props(sendingActor: ActorRef) = Props(new EncodingActor(sendingActor))
}
