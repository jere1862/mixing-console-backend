package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class AudioNode(id: Int,
                     name: String,
                     volumeSlider: Int,
                     lowSlider: Int,
                     medSlider: Int,
                     highSlider: Int,
                     volume: Int,
                     low: Int,
                     med: Int,
                     high: Int,
                     latitude: Float,
                     longitude: Float,
                     isFix: Boolean,
                     markerOpacity: Float,
                     gpsDataSet: Boolean,
                     micDataSet: Boolean,
                     isAdjustedAutomatically: Boolean){
  def hasMicAndDataSet = micDataSet && gpsDataSet
}

object AudioNode {
  implicit val audioNodeReads = Json.reads[AudioNode]
  implicit val nodeWrites = Json.writes[AudioNode]

  def applyFromDataModel(dataModel: DataModel): AudioNode = {
    dataModel match  {
      case gpsData: GpsDataModel => {
        new AudioNode(gpsData.id,
                      gpsData.id.toString,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      gpsData.latitude,
                      gpsData.longitude,
                      false,
                      1.0f,
                      true,
                      false,
                      false)
      }
      case micData: MicrophoneDataModel => {
        new AudioNode(micData.id,
                      micData.id.toString,
                      0,
                      0,
                      0,
                      0,
                      micData.volume,
                      micData.low,
                      micData.med,
                      micData.high,
                      0.0f,
                      0.0f,
                      micData.isFix,
                      1.0f,
                      false,
                      true,
                      false)
      }
      case micData: MicrophoneWithSlidersDataModel => {
          new AudioNode(micData.id,
                        micData.id.toString,
                        micData.volumeSlider,
                        micData.lowSlider,
                        micData.medSlider,
                        micData.highSlider,
                        micData.volume,
                        micData.low,
                        micData.med,
                        micData.high,
                        0.0f,
                        0.0f,
                        micData.isFix,
                        1.0f,
                        false,
                        true,
                        true)
      }

    }
  }
}
