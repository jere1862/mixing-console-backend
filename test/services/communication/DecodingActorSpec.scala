package services.communication

import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

import akka.testkit.TestProbe
import com.typesafe.config.Config
import models.{GpsDataModel, MicrophoneDataModel, MicrophoneGainDataModel, MicrophoneWithSlidersDataModel}
import org.mockito.Mockito

import scala.concurrent.duration.FiniteDuration

class DecodingActorSpec extends BaseActorSpec {
  val prob1 = TestProbe()
  val prob2 = TestProbe()

  val config = mock[Config]
  Mockito.when(config.getInt("lowFactor")).thenReturn(1)
  Mockito.when(config.getInt("medFactor")).thenReturn(1)
  Mockito.when(config.getInt("highFactor")).thenReturn(1)

  "A Decoding Actor " should {

  val decodingActor =  system.actorOf(DecodingActor.props(prob1.ref, config))
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
        0xFF, // VOLUME
        0x00, // VOLUME
        0x00, // VOLUME
        0x00, // VOLUME
        0xFF, // LOW
        0x01, // LOW
        0x00, // LOW
        0x00, // LOW
        0x00, // MEDIUM
        0x00, // MEDIUM
        0x00, // MEDIUM
        0x00, // MEDIUM
        0x01, // HIGH
        0x00, // HIGH
        0x00, // HIGH
        0x00, // HIGH
      ).map(_.toByte)

      decodingActor ! ByteBuffer.wrap(bytes)

      val micDataModel: MicrophoneDataModel = prob1.expectMsgClass(classOf[MicrophoneDataModel])

      micDataModel.id shouldBe 2
      micDataModel.isFix shouldBe false
      micDataModel.volume shouldBe 255
      micDataModel.low shouldBe 511
      micDataModel.med shouldBe 0
      micDataModel.high shouldBe 1
    }

    "decode a fix node microphone data message" in {
      val bytes:Array[Byte] = Array(
        0x11, // HEADER
        0x2, // ID
        0x7F, // VOLUME
        0x00, // VOLUME
        0x00, // VOLUME
        0x00, // VOLUME
        0xFF, // LOW
        0x01, // LOW
        0x00, // LOW
        0x00, // LOW
        0x00, // MEDIUM
        0x00, // MEDIUM
        0x00, // MEDIUM
        0x00, // MEDIUM
        0x01, // HIGH
        0x00, // HIGH
        0x00, // HIGH
        0x00, // HIGH
      ).map(_.toByte)

      decodingActor ! ByteBuffer.wrap(bytes)

      val micDataModel: MicrophoneDataModel = prob1.expectMsgClass(classOf[MicrophoneDataModel])

      micDataModel.id shouldBe 2
      micDataModel.isFix shouldBe true
      micDataModel.volume shouldBe 127
      micDataModel.low shouldBe 511
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
      ).map(_.toByte)

      decodingActor ! ByteBuffer.wrap(bytes)

      val micDataModel: MicrophoneGainDataModel = prob1.expectMsgClass(classOf[MicrophoneGainDataModel])

      micDataModel.id shouldBe 2
      micDataModel.isFix shouldBe false
      micDataModel.volumeSlider shouldBe 127
      micDataModel.lowSlider shouldBe 255
      micDataModel.medSlider shouldBe 0
      micDataModel.highSlider shouldBe 1
    }

    "throw an error if data has a wrong header" in {
      val bytes:Array[Byte] = Array(
        0xCD // HEADER
      ).map(_.toByte)

      decodingActor ! ByteBuffer.wrap(bytes)

      prob1.expectNoMessage(FiniteDuration(2, TimeUnit.SECONDS))
    }
  }
}
