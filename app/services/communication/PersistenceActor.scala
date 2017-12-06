package services.communication

import akka.actor.{Actor, Props}
import models._
import play.api.Logger
import services.node.NodeService

class PersistenceActor(nodeService: NodeService) extends Actor{
  def receive = {
    case data: DataModel =>
      val id = data.getId
      Logger.debug("Received a data model, converting it to a node.")
      nodeService.get(data.getId()) match {
        case Some(node) => {
          Logger.debug(s"Modifiying node $id.")
          val modifiedNode = createModifiedNode(node, data)
          nodeService.save(modifiedNode)
        }
        case None => {
          Logger.debug(s"Couldn't find an existing node with id $id, creating a new one.")
          nodeService.save(AudioNode.applyFromDataModel(data))
        }
      }
    case _ =>
      Logger.debug("Received something other than a data model.")
  }

  def createModifiedNode(node: AudioNode, newData: DataModel): AudioNode = {
    newData match {
      case gpsData: GpsDataModel =>
        node.copy(latitude = gpsData.latitude,
          longitude = gpsData.longitude, gpsDataSet = true)
      case micData: MicrophoneDataModel =>
        node.copy(volume = micData.volume, low = micData.low,
          med = micData.med, high = micData.high, micDataSet = true)
      case gainData: MicrophoneGainDataModel =>
        node.copy(volumeSlider = gainData.volumeSlider, lowSlider = gainData.lowSlider,
          medSlider = gainData.medSlider, highSlider = gainData.highSlider,
          isAdjustedAutomatically = true)
      case volumeData: MicrophoneVolumeDataModel =>
        node.copy(volumeSlider = volumeData.volumeSlider)
    }
  }
}

object PersistenceActor{
  def props(nodeService: NodeService) = Props(new PersistenceActor(nodeService))
}
