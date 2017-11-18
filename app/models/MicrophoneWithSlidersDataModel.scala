package models

import play.api.libs.json.Json

case class MicrophoneWithSlidersDataModel(id: Int,
                                          isFix: Boolean,
                                          volumeSlider: Short,
                                          lowSlider: Short,
                                          medSlider: Short,
                                          highSlider: Short,
                                          volume: Short,
                                          low: Short,
                                          med: Short,
                                          high: Short) extends DataModel{
  def getId = id
}

object MicrophoneWithSlidersDataModel {
  implicit val microphoneWithSlidersDataModelReads = Json.reads[MicrophoneWithSlidersDataModel]
}
