package services.communication

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Udp
import akka.io.IO
import akka.util.ByteString

class UDPReceivingActor(persistenceActor: ActorRef) extends Actor{
  import context.system
  val localAddress  = new InetSocketAddress("localhost", 1337)
  IO(Udp) ! Udp.Bind(self, localAddress)

  def receive = {
    case Udp.Bound(localAddress) =>
      context.become(ready(sender()))
  }

  def ready(socket: ActorRef): Receive = {
    case Udp.Received(data, remote) =>
      val child = context.actorOf(ParsingActor.props(persistenceActor))
      child ! data.asByteBuffer
      socket ! Udp.Send(ByteString("Message received!"), remote) // Echo back
    case Udp.Unbind => socket ! Udp.Unbind
    case Udp.Unbound => context.stop(self)
  }
}

object UDPReceivingActor {
  def props(actorRef: ActorRef) = Props(new UDPReceivingActor(actorRef))
}
