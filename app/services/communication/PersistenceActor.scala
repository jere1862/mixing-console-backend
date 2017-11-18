package services.communication

import akka.actor.{Actor, Props}
import models._
import services.node.NodeService

class PersistenceActor(nodeService: NodeService) extends Actor{
  def receive = {
    case data: DataModel =>
      nodeService.get(data.getId) match {
        case Some(node) => {
          val modifiedNode = createModifiedNode(node, data)
          nodeService.save(modifiedNode)
        }
        case None => {
          nodeService.save(AudioNode.applyFromDataModel(data))
        }
      }
  }

  def createModifiedNode(node: AudioNode, newData: DataModel): AudioNode = {
    newData match {
      case gpsData: GpsDataModel =>
        node.copy(latitude = gpsData.latitude,
          longitude = gpsData.longitude, gpsDataSet = true)
      case micData: MicrophoneDataModel =>
        node.copy(volume = micData.volume, low = micData.low,
          med = micData.med, high = micData.high, micDataSet = true,
          isAdjustedAutomatically = false)
      case micData: MicrophoneWithSlidersDataModel =>
        node.copy(volumeSlider = micData.volumeSlider, lowSlider = micData.lowSlider,
          medSlider = micData.medSlider, highSlider = micData.highSlider,
          volume = micData.volume, low = micData.low,
          med = micData.med, high = micData.high, micDataSet = true,
          isAdjustedAutomatically = true)
    }
  }
}


object PersistenceActor{
  def props(nodeService: NodeService) = Props(new PersistenceActor(nodeService))
}
