package services.communication

import java.nio.ByteBuffer

import akka.actor.{Actor, ActorRef, Props}
import models.{DataModel, GpsDataModel, MicrophoneDataModel}

class ParsingActor(persistenceActor: ActorRef) extends Actor{
  val MicrophoneMessageLength = 7;
  val GpsMessageLength = 10;
  val BitMaskFixFlag = 0x1;

  def receive = {
    case byteB: ByteBuffer =>
      println("Received message")
      val dataModel = parseByteBuffer(byteB)
      dataModel match {
        case Some(data) => persistenceActor ! data
        case None => println("Failed parsing message received by UDP.")
      }
    case _ => println("Error parsing bytebuffer.")
  }

  def parseByteBuffer(byteBuffer: ByteBuffer): Option[DataModel] = {
    if(byteBuffer.limit() == GpsMessageLength){
      Option.apply(parseGpsMessage(byteBuffer))
    }else if(byteBuffer.limit() == MicrophoneMessageLength){
      Option.apply(parseMicrophoneMessage(byteBuffer))
    }else{
      Option.empty
    }
  }

  def parseGpsMessage(byteBuffer: ByteBuffer): GpsDataModel ={
    println("Received gps message")

    new GpsDataModel(byteBuffer.getChar, byteBuffer.getFloat, byteBuffer.getFloat)
  }

  def parseMicrophoneMessage(byteBuffer: ByteBuffer): MicrophoneDataModel = {
    println("Received microphone data")

    new MicrophoneDataModel(byteBuffer.getChar, (byteBuffer.get & BitMaskFixFlag) == 1,
      byteBuffer.get, byteBuffer.get, byteBuffer.get, byteBuffer.get)
  }
}

object ParsingActor {
  def props(actorRef: ActorRef) = Props(new ParsingActor(actorRef))
}