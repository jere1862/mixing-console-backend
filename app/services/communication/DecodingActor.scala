package services.communication

import java.nio.ByteBuffer

import akka.actor.{Actor, ActorRef, Props}
import models.{DataModel, GpsDataModel, MicrophoneDataModel, MicrophoneWithSlidersDataModel}
import play.api.Logger

class DecodingActor(persistenceActor: ActorRef) extends Actor{
  val MicrophoneMessageLength = 6
  val GpsMessageLength = 9
  val BitMaskFixFlag = 0x1
  val headerMask = (1 << 4) - 1
  val flagsMask = headerMask << 4

  val HeaderMicrophoneData = 0x1
  val HeaderMicrophoneWithSlidersData = 0x2
  val HeaderGpsData = 0x3

  val logger = Logger(this.getClass)

  def receive = {
    case byteB: ByteBuffer =>
      try{
        val dataModel = decodeByteBuffer(byteB)
        dataModel match {
          case Some(data) => persistenceActor ! data
          case None => logger.debug("Failed parsing message received by UDP.")
        }
      }catch {
        case e: Exception =>
          e.printStackTrace()
      }
    case _ => logger.debug("Error parsing bytebuffer.")
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
    }
  }

  def parseGpsMessage(byteBuffer: ByteBuffer): GpsDataModel ={
    logger.debug("Received gps message")
    new GpsDataModel(byteBuffer.get, byteBuffer.getFloat, byteBuffer.getFloat)
  }

  def parseMicrophoneMessage(byteBuffer: ByteBuffer, flags: Byte): MicrophoneDataModel = {
    logger.debug("Received microphone data")

    new MicrophoneDataModel(byteBuffer.get & 0xFF, (flags & BitMaskFixFlag) == 1,
      byteBuffer.get & 0xFF, byteBuffer.get & 0xFF, byteBuffer.get & 0xFF, byteBuffer.get & 0xFF)
  }

  def parseMicrophoneWithSlidersMessage(byteBuffer: ByteBuffer, flags: Byte): MicrophoneWithSlidersDataModel = {
    logger.debug("Received microphone and sliders data")

    new MicrophoneWithSlidersDataModel(byteBuffer.get & 0xFF, (flags & 1) == 1, byteBuffer.get & 0xFF,
      byteBuffer.get & 0xFF, byteBuffer.get & 0xFF, byteBuffer.get & 0xFF, byteBuffer.get & 0xFF,
      byteBuffer.get & 0xFF, byteBuffer.get & 0xFF, byteBuffer.get & 0xFF)
  }
}

object DecodingActor {
  def props(actorRef: ActorRef) = Props(new DecodingActor(actorRef))
}
