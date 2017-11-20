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
        node.copy(volume = micData.volume.toInt, low = micData.low.toInt,
          med = micData.med.toInt, high = micData.high.toInt, micDataSet = true,
          isAdjustedAutomatically = false)
      case micData: MicrophoneWithSlidersDataModel =>
        node.copy(volumeSlider = micData.volumeSlider.toInt, lowSlider = micData.lowSlider.toInt,
          medSlider = micData.medSlider.toInt, highSlider = micData.highSlider.toInt,
          volume = micData.volume.toInt, low = micData.low.toInt,
          med = micData.med.toInt, high = micData.high.toInt, micDataSet = true,
          isAdjustedAutomatically = true)
    }
  }
}


object PersistenceActor{
  def props(nodeService: NodeService) = Props(new PersistenceActor(nodeService))
}
