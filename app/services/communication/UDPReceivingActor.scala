package services.communication

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.io.Udp
import akka.io.IO
import akka.util.ByteString

class UDPReceivingActor extends Actor{
  import context.system
  IO(Udp) ! Udp.Bind(self, new InetSocketAddress("localhost", 1338))

  def receive = {
    case Udp.Bound(local) =>
      context.become(ready(sender()))
  }

  def ready(socket: ActorRef): Receive = {
    case Udp.Received(data, remote) =>
      val str: String = data.utf8String
      println(str)
      socket ! Udp.Send(ByteString("Message received!"), remote) // example server echoes back
    case Udp.Unbind => socket ! Udp.Unbind
    case Udp.Unbound => context.stop(self)
  }
}
