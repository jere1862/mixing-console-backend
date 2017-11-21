package models

import play.api.libs.json.Json

case class MicrophoneWithSlidersDataModel(id: Int,
                                          isFix: Boolean,
                                          volumeSlider: Int,
                                          lowSlider: Int,
                                          medSlider:  Int,
                                          highSlider: Int,
                                          volume: Int,
                                          low: Int,
                                          med: Int,
                                          high: Int) extends DataModel{
  def getId = id
}
