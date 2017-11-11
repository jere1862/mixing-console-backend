package services.communication

import java.nio.ByteBuffer

import akka.actor.{Actor, ActorRef, Props}
import models.NotifyNodeSoundChangeModel

class EncodingActor(sendingActor: ActorRef) extends Actor {
  val NotifyNodeSoundChangeMessageLength: Int = 4;

  def receive = {
    case notifyNode:NotifyNodeSoundChangeModel =>
      val byteBuffer = ByteBuffer.allocate(NotifyNodeSoundChangeMessageLength)
      byteBuffer.putChar(notifyNode.id.toChar)
      byteBuffer.put(notifyNode.sliderType.toByte)
      byteBuffer.put(notifyNode.value.toByte)
      sendingActor ! byteBuffer
    case adjustAutomatically: Boolean =>
      // Send adjustAutomatically message to sendingActor
    case _ =>
      println("What happened")
  }
}

object EncodingActor {
  def props(sendingActor: ActorRef) = Props(new EncodingActor(sendingActor))
}
