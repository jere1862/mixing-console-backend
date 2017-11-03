package services.communication

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.io.Udp
import akka.io.IO
import akka.util.ByteString

class UDPSendingActor extends Actor{
  val remoteAddress: InetSocketAddress = new InetSocketAddress("localhost", 3000)
  import context.system
  IO(Udp) ! Udp.SimpleSender

  def receive = {
    case Udp.SimpleSenderReady =>
      context.become(ready(sender()))
  }

  def ready(send: ActorRef): Receive = {
    case msg: String =>
      send ! Udp.Send(ByteString(msg), remoteAddress)
  }
}
