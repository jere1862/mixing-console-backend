package services.communication

import java.net.InetSocketAddress
import java.nio.ByteBuffer

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Udp
import akka.io.IO
import akka.util.ByteString
import play.api.Logger

class UDPSendingActor(remoteAddress: InetSocketAddress) extends Actor{
  import context.system
  IO(Udp) ! Udp.SimpleSender
  var iNetAddress = remoteAddress

  def receive = {
    case Udp.SimpleSenderReady =>
      context.become(ready(sender()))
  }

  def ready(send: ActorRef): Receive = {
    case msg: ByteBuffer =>
      Logger.debug(s"Sending actor relaying message to ${iNetAddress.toString}")
      send ! Udp.Send(ByteString.apply(msg.array), iNetAddress)
    case address: InetSocketAddress =>
      iNetAddress = address
  }
}

object UDPSendingActor {
  def props(inetSocketAddress: InetSocketAddress) = Props(new UDPSendingActor(inetSocketAddress))
}
