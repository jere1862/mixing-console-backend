package services.communication

import java.nio.{ByteBuffer, ByteOrder}

import akka.actor.{Actor, ActorRef, Props}
import com.typesafe.config.Config
import models._
import play.api.Logger

class DecodingActor(persistenceActor: ActorRef, configuration: Config) extends Actor{
  val BitMaskFixFlag = 0x1
  val headerMask = (1 << 4) - 1
  val flagsMask = headerMask << 4

  val HeaderMicrophoneData = 0x1
  val MicrophoneGainData = 0x2
  val HeaderGpsData = 0x3
  val MicrophoneVolumeData = 0x4

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
      case MicrophoneGainData =>
        Option.apply(parseMicrophoneGainData(byteBuffer, flags))
      case MicrophoneVolumeData =>
        Option.apply(parseMicrophoneVolumeData(byteBuffer, flags))
      case _ =>
        Logger.debug(s"Received unknown header: $header, accepted values are $HeaderMicrophoneData, $MicrophoneGainData, or $HeaderGpsData.")
        Option.empty
    }
  }

  def parseGpsMessage(byteBuffer: ByteBuffer): GpsDataModel ={
    Logger.debug("Received gps message")
    new GpsDataModel(byteBuffer.get, byteBuffer.getFloat, byteBuffer.getFloat)
  }

  def parseMicrophoneMessage(byteBuffer: ByteBuffer, flags: Byte): MicrophoneDataModel = {
    Logger.debug("Received microphone data")

    new MicrophoneDataModel(unsigned(byteBuffer.get), getFixBoolean(flags),
      byteBuffer.getInt, byteBuffer.getInt/LowFactor, byteBuffer.getInt/MedFactor, byteBuffer.getInt/HighFactor)
  }

  def parseMicrophoneGainData(byteBuffer: ByteBuffer, flags: Byte): MicrophoneGainDataModel = {
    Logger.debug("Received microphone gain values")

    new MicrophoneGainDataModel(unsigned(byteBuffer.get), getFixBoolean(flags), unsigned(byteBuffer.get),
      unsigned(byteBuffer.get), unsigned(byteBuffer.get), unsigned(byteBuffer.get))
  }

  def parseMicrophoneVolumeData(byteBuffer: ByteBuffer, flags: Byte): MicrophoneVolumeDataModel = {
    Logger.debug("Received microphone volume data")

    new MicrophoneVolumeDataModel(unsigned(byteBuffer.get), getFixBoolean(flags), unsigned(byteBuffer.get))
  }

  private def unsigned(byte: Byte):Int = byte & 0xFF
  private def getFixBoolean(flags: Byte): Boolean = (flags & BitMaskFixFlag) == 1
}

object DecodingActor {
  def props(actorRef: ActorRef, configuration: Config) = Props(new DecodingActor(actorRef, configuration))
}
