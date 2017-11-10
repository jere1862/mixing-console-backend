package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class AudioNode(id: Int, name: String, volume: Short, low: Short,
                     med: Short, high: Short, latitude: Float, longitude: Float,
                     isFix: Boolean, markerOpacity: Float, gpsDataSet: Boolean, micDataSet: Boolean) {
}

object AudioNode {
  implicit val audioNodeReads = Json.reads[AudioNode]
  implicit val nodeWrites = Json.writes[AudioNode]

  def applyFromDataModel(dataModel: DataModel): AudioNode = {
    dataModel match  {
      case gpsData: GpsDataModel => {
        new AudioNode(gpsData.id, "", 0, 0, 0, 0, gpsData.latitude,
          gpsData.longitude, false, 1.0f, true, false)
      }
      case micData: MicrophoneDataModel => {
        new AudioNode(micData.id, "", micData.volume, micData.low, micData.med,
          micData.high, 0.0f, 0.0f, micData.isFix, 1.0f, false, true)
      }
    }
  }
}
