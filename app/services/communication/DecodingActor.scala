package services.communication

import java.nio.{ByteBuffer, ByteOrder}

import akka.actor.{Actor, ActorRef, Props}
import com.typesafe.config.Config
import models.{DataModel, GpsDataModel, MicrophoneDataModel, MicrophoneWithSlidersDataModel}
import play.api.Logger

class DecodingActor(persistenceActor: ActorRef, configuration: Config) extends Actor{
  val BitMaskFixFlag = 0x1
  val headerMask = (1 << 4) - 1
  val flagsMask = headerMask << 4

  val HeaderMicrophoneData = 0x1
  val HeaderMicrophoneWithSlidersData = 0x2
  val HeaderGpsData = 0x3

  val LowFactor = configuration.getInt("lowFactor")
  val MedFactor = configuration.getInt("medFactor")
  val HighFactor = configuration.getInt("highFactor")

  def receive = {
      case byteB: ByteBuffer =>
        byteB.order(ByteOrder.LITTLE_ENDIAN)
        try{
          val dataModel = decodeByteBuffer(byteB)
          dataModel match {
            case Some(data) => persistenceActor ! data
            case None => Logger.debug("Failed parsing message received by UDP.")
          }
        }catch {
          case e: Exception =>
            e.printStackTrace()
        }
    case _ => Logger.debug("Error parsing bytebuffer.")
  }

  def decodeByteBuffer(byteBuffer: ByteBuffer): Option[DataModel] = {
    val flagsAndHeader: Byte = byteBuffer.get
    val header = flagsAndHeader & headerMask
    val flags = ((flagsAndHeader & flagsMask) >> 4).toByte

    header match{
      case HeaderMicrophoneData =>
        Option.apply(parseMicrophoneMessage(byteBuffer, flags))
      case HeaderGpsData =>
        Option.apply(parseGpsMessage(byteBuffer))
      case HeaderMicrophoneWithSlidersData =>
        Option.apply(parseMicrophoneWithSlidersMessage(byteBuffer, flags))
      case _ =>
        Logger.debug(s"Received unknown header: $header, accepted values are $HeaderMicrophoneData, $HeaderMicrophoneWithSlidersData, or $HeaderGpsData.")
        Option.empty
    }
  }

  def parseGpsMessage(byteBuffer: ByteBuffer): GpsDataModel ={
    Logger.debug("Received gps message")
    new GpsDataModel(byteBuffer.get, byteBuffer.getFloat, byteBuffer.getFloat)
  }

  def parseMicrophoneMessage(byteBuffer: ByteBuffer, flags: Byte): MicrophoneDataModel = {
    Logger.debug("Received microphone data")

    new MicrophoneDataModel(unsigned(byteBuffer.get), (flags & BitMaskFixFlag) == 1,
      byteBuffer.getInt, byteBuffer.getInt/LowFactor, byteBuffer.getInt/MedFactor, byteBuffer.getInt/HighFactor)
  }

  def parseMicrophoneWithSlidersMessage(byteBuffer: ByteBuffer, flags: Byte): MicrophoneWithSlidersDataModel = {
    Logger.debug("Received microphone and sliders data")

    new MicrophoneWithSlidersDataModel(unsigned(byteBuffer.get), (flags & 1) == 1, unsigned(byteBuffer.get),
      unsigned(byteBuffer.get), unsigned(byteBuffer.get), unsigned(byteBuffer.get), byteBuffer.getInt,
      byteBuffer.getInt/LowFactor, byteBuffer.getInt/MedFactor, byteBuffer.getInt/HighFactor)
  }

  private def unsigned(byte: Byte):Int = byte & 0xFF
  private def scaled(value: Int) = value.toInt
}

object DecodingActor {
  def props(actorRef: ActorRef, configuration: Config) = Props(new DecodingActor(actorRef, configuration))
}
