package models

import play.api.libs.json.Json

case class NotifyChangeModel(sliderType: Int, value: Int){
}

object NotifyChangeModel {
  implicit val notifyChangeModelReads = Json.reads[NotifyChangeModel]
}