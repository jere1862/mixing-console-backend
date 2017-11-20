package services.communication


import java.nio.ByteBuffer

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import models.{GpsDataModel, MicrophoneDataModel, MicrophoneWithSlidersDataModel}
import org.scalatest.mockito.MockitoSugar

class DecodingActorSpec extends TestKit(ActorSystem("MySpec"))
  with ImplicitSender
  with WordSpecLike
  with MockitoSugar
  with Matchers
  with BeforeAndAfterAll{

  val prob1 = TestProbe()
  val decodingActor =  system.actorOf(DecodingActor.props(prob1.ref))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A Decoding Actor " should {

    "decode a gps message" in {
      val gpsMessage = ByteBuffer.wrap(Array(
        0x3, // HEADER
        0x2, // ID
        0x0, // LAT0
        0x0, // LAT1
        0x0, // LAT2
        0x0, // LAT3
        0x11,// LONG0
        0x11,// LONG1
        0x11,// LONG2
        0x11 // LONG3
      ))

      decodingActor ! gpsMessage

      val gpsDataModel: GpsDataModel = prob1.expectMsgClass(classOf[GpsDataModel])

      gpsDataModel.id shouldBe 2
      gpsDataModel.latitude shouldBe 0
      gpsDataModel.longitude - 1.1443742E-28 shouldBe 0.0 +- 0.00000000001
    }

    "decode a mobile node microphone data message" in {
      val bytes:Array[Byte] = Array(
        0x1, // HEADER
        0x2, // ID
        0x7F, // VOLUME
        0xFF, // LOW
        0x00, // MEDIUM
        0x01, // HIGH
      ).map(_.toByte)

      decodingActor ! ByteBuffer.wrap(bytes)

      val micDataModel: MicrophoneDataModel = prob1.expectMsgClass(classOf[MicrophoneDataModel])

      micDataModel.id shouldBe 2
      micDataModel.isFix shouldBe false
      micDataModel.volume shouldBe 127
      micDataModel.low shouldBe 255
      micDataModel.med shouldBe 0
      micDataModel.high shouldBe 1
    }

    "decode a fix node microphone data message" in {
      val bytes:Array[Byte] = Array(
        0x11, // HEADER
        0x2, // ID
        0x7F, // VOLUME
        0xFF, // LOW
        0x00, // MEDIUM
        0x01, // HIGH
      ).map(_.toByte)

      decodingActor ! ByteBuffer.wrap(bytes)

      val micDataModel: MicrophoneDataModel = prob1.expectMsgClass(classOf[MicrophoneDataModel])

      micDataModel.id shouldBe 2
      micDataModel.isFix shouldBe true
      micDataModel.volume shouldBe 0x7F
      micDataModel.low shouldBe 0xFF
      micDataModel.med shouldBe 0
      micDataModel.high shouldBe 1
    }

    "decode a microphone data with sliders message" in {
      val bytes:Array[Byte] = Array(
        0x2, // HEADER
        0x2, // ID
        0x7F, // VOLSLIDER
        0xFF, // LOWSLIDER
        0x00, // MEDSLIDER
        0x01, // HIGHSLIDER
        0x7F, // VOLUME
        0xFF, // LOW
        0x00, // MEDIUM
        0x01, // HIGH
      ).map(_.toByte)

      decodingActor ! ByteBuffer.wrap(bytes)

      val micDataModel: MicrophoneWithSlidersDataModel = prob1.expectMsgClass(classOf[MicrophoneWithSlidersDataModel])

      micDataModel.id shouldBe 2
      micDataModel.isFix shouldBe false
      micDataModel.volumeSlider shouldBe 127
      micDataModel.lowSlider shouldBe 255
      micDataModel.medSlider shouldBe 0
      micDataModel.highSlider shouldBe 1
      micDataModel.volume shouldBe 127
      micDataModel.low shouldBe 255
      micDataModel.med shouldBe 0
      micDataModel.high shouldBe 1
    }
  }
}
