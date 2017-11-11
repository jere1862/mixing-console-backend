package models

import play.api.libs.json.Json

case class NotifyNodeSoundChangeModel(id: Int, sliderType: Int, value: Int){
}

object NotifyNodeSoundChangeModel {
  implicit val notifyChangeModelReads = Json.reads[NotifyNodeSoundChangeModel]
}
