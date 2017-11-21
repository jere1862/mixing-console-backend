package services.communication

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Udp
import akka.io.IO
import akka.util.ByteString

class UDPReceivingActor(persistenceActor: ActorRef, receivingAddress: InetSocketAddress) extends Actor {
  import context.system
  IO(Udp) ! Udp.Bind(self, receivingAddress)

  def receive = {
    case Udp.Bound(receivingAddress) =>
      context.become(ready(sender()))
  }

  def ready(socket: ActorRef): Receive = {
    case Udp.Received(data, remote) =>
      val child = context.actorOf(DecodingActor.props(persistenceActor))
      child ! data.asByteBuffer
      socket ! Udp.Send(ByteString("Message received!"), remote) // Echo back
    case Udp.Unbind => socket ! Udp.Unbind
    case Udp.Unbound => context.stop(self)
  }
}

object UDPReceivingActor {
  def props(actorRef: ActorRef, receivingAddress: InetSocketAddress) =
    Props(new UDPReceivingActor(actorRef, receivingAddress))
}
