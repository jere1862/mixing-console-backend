package functional

import java.net.{InetAddress, InetSocketAddress}
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.{IO, Udp}
import akka.testkit.TestProbe
import models.{AudioNode, NotifyAutomaticAdjustmentModel, NotifyNodeSoundChangeModel, NotifySoundLimitedModel}
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.{GuiceOneServerPerSuite}
import play.api.Configuration
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import play.api.libs.ws._
import services.communication.UDPSendingActor
import org.scalatest.concurrent._
import play.api.libs.json._

class NodeServerSpec extends PlaySpec
  with GuiceOneServerPerSuite
  with DefaultAwaitTimeout
  with FutureAwaits
  with Eventually
  with BeforeAndAfterAll{
  val system = ActorSystem("FunctionalTestSystem")
  val probe = new TestProbe(system)
  val config = Configuration.load(app.environment)
  val receivingPort = config.underlying.getInt("receiving.port")
  val destinationAddres = new InetSocketAddress("localhost", 3000)
  val udpAddress = new InetSocketAddress("localhost", receivingPort)
  val udpSender = system.actorOf(UDPSendingActor.props(udpAddress), "UdpSender")
  val udpReceiver = system.actorOf(TestActor.props(probe.ref, destinationAddres))
  val wsClient = app.injector.instanceOf[WSClient]

  "the server" should {
    "add nodes to the database" in {
      val address = s"http://localhost:$port/api/nodes"

      udpSender ! NodeServerSpec.getGpsMessage(1)
      udpSender ! NodeServerSpec.getMicrophoneMessage(1)
      udpSender ! NodeServerSpec.getGpsMessage(2)
      udpSender ! NodeServerSpec.getMicrophoneMessage(2)

      eventually {
        val response = await(wsClient.url(address).get(), 2, TimeUnit.SECONDS)

        response.status mustBe 200
        val audioNodes = fromApiResponse(response)

        audioNodes must have length (2)

        val idList: List[Int] = audioNodes.map(node => node.id)

        idList must contain(1)
        idList must contain(2)
      }
    }

    "handle microphone data that is adjusted automatically" in {
      udpSender ! NodeServerSpec.getGpsMessage(1)
      udpSender ! NodeServerSpec.getMicrophoneMessage(1)

      val address = s"http://localhost:$port/api/nodes"

      eventually {
        val response = await(wsClient.url(address).get(), 2, TimeUnit.SECONDS)

        response.status mustBe 200
        val audioNodes = fromApiResponse(response)

        audioNodes.find(node => node.id == 1).get.isAdjustedAutomatically must be (false)
      }

      udpSender ! NodeServerSpec.getMicrophoneWithSlidersMessage(1)

      eventually {
        val response = await(wsClient.url(address).get(), 2, TimeUnit.SECONDS)

        response.status mustBe 200
        val audioNodes = fromApiResponse(response)

        audioNodes.find(node => node.id == 1).get.isAdjustedAutomatically must be (true)
      }
    }

    "handle a change notification" in {
      val address = s"http://localhost:$port/api/nodes/change"

      val json = Json.toJsObject(NotifyNodeSoundChangeModel(1,2,3))

      val response = await(wsClient.url(address).put(json), 5, TimeUnit.SECONDS)

      response.status must be (200)
    }

    "handle automatic adjustment" in {
      val address = s"http://localhost:$port/api/nodes/automaticAdjustment"

      val json = Json.toJsObject(NotifyAutomaticAdjustmentModel(1, true))

      val response = await(wsClient.url(address).put(json), 2, TimeUnit.SECONDS)

      response.status must be (200)
    }

    "handle sound limiting" in {
      val address = s"http://localhost:$port/api/nodes/limitVolume"

      val json = Json.toJsObject(NotifySoundLimitedModel(true))

      val response = await(wsClient.url(address).put(json), 2, TimeUnit.SECONDS)

      response.status must be (200)
    }
  }

  def fromApiResponse(response: WSResponse): List[AudioNode] = {
    val responseAsAudioNodes = response.body[JsValue].validate[List[AudioNode]]

    responseAsAudioNodes match {
      case c :JsSuccess[List[AudioNode]] =>
        c.get
      case e: JsError =>
        fail("An error occurred while parsing the node list json from the api")
    }
  }

}

object NodeServerSpec {

    private[this] def getMessage(values: Array[Int]): ByteBuffer = {
      val bytes = values.map(_.toByte)
      ByteBuffer.wrap(bytes)
    }

    def getMicrophoneMessage(id: Int) = {
      getMessage(
        Array(
          0x1, // HEADER
          id, // ID
          0xFF, // VOLUME
          0x7F, // VOLUME
          0x00, // VOLUME
          0x00, // VOLUME
          0xAB, // LOW
          0xFF, // LOW
          0x00, // LOW
          0x00, // LOW
          0x01, // MEDIUM
          0x00, // MEDIUM
          0x00, // MEDIUM
          0x00, // MEDIUM
          0xFF, // HIGH
          0x01, // HIGH
          0x00, // HIGH
          0x00, // HIGH
        )
      )
    }

    def getGpsMessage(id: Int): ByteBuffer = {
      getMessage(
        Array(
          0x3, // HEADER
          id, // ID
          0x0, // LAT0
          0x0, // LAT1
          0x0, // LAT2
          0x0, // LAT3
          0x11,// LONG0
          0x11,// LONG1
          0x11,// LONG2
          0x11 // LONG3
        )
      )
    }

    def getMicrophoneWithSlidersMessage(id: Int): ByteBuffer = {
      getMessage(
        Array(
          0x2, // HEADER
          id, // ID
          0x7F, // VOLSLIDER
          0xFF, // LOWSLIDER
          0x00, // MEDSLIDER
          0x01, // HIGHSLIDER
        )
      )
    }
}

sealed class TestActor(actorRef: ActorRef, receivingAddress: InetSocketAddress) extends Actor {
  import context.system
  IO(Udp) ! Udp.Bind(self, receivingAddress)

  def receive = {
    case Udp.Bound(receivingAddress) =>
      context.become(ready(sender()))
  }

  def ready(socket: ActorRef): Receive = {
    case Udp.Received(data, remote) =>
      println("Received UDP message")
      actorRef ! data.asByteBuffer
    case Udp.Unbind => socket ! Udp.Unbind
    case Udp.Unbound => context.stop(self)
  }
}

object TestActor {
  def props(actorRef: ActorRef, receivingAddress: InetSocketAddress) = Props(new TestActor(actorRef,receivingAddress))
}
