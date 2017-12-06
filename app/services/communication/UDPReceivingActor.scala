package services.communication

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Udp
import akka.io.IO
import com.typesafe.config.Config

class UDPReceivingActor(persistenceActor: ActorRef, receivingAddress: InetSocketAddress, configuration: Config, sendingActor: ActorRef) extends Actor {
  import context.system
  IO(Udp) ! Udp.Bind(self, receivingAddress)

  def receive = {
    case Udp.Bound(receivingAddress) =>
      context.become(ready(sender()))
  }

  def ready(socket: ActorRef): Receive = {
    case Udp.Received(data, remote) =>
      val child = context.actorOf(DecodingActor.props(persistenceActor, configuration))
      child ! data.asByteBuffer
      sendingActor ! remote
    case Udp.Unbind => socket ! Udp.Unbind
    case Udp.Unbound => context.stop(self)
  }
}

object UDPReceivingActor {
  def props(actorRef: ActorRef, receivingAddress: InetSocketAddress, configuration: Config, sendingActor: ActorRef) =
    Props(new UDPReceivingActor(actorRef, receivingAddress, configuration, sendingActor))
}
