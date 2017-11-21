package services.communication

import java.net.InetSocketAddress
import java.nio.ByteBuffer

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Udp
import akka.io.IO
import akka.util.ByteString

class UDPSendingActor(remoteAddress: InetSocketAddress) extends Actor{
  import context.system
  IO(Udp) ! Udp.SimpleSender

  def receive = {
    case Udp.SimpleSenderReady =>
      context.become(ready(sender()))
  }

  def ready(send: ActorRef): Receive = {
    case msg: ByteBuffer =>
      send ! Udp.Send(ByteString.apply(msg.array), remoteAddress)
  }
}

object UDPSendingActor {
  def props(inetSocketAddress: InetSocketAddress) = Props(new UDPSendingActor(inetSocketAddress))
}
