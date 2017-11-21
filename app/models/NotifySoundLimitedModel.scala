package models

import play.api.libs.json.Json

case class NotifySoundLimitedModel(isSoundLimited: Boolean) {
}

object NotifySoundLimitedModel {
  implicit val notifySoundLimitedModelReads = Json.reads[NotifySoundLimitedModel]
  implicit val notifySoundLimitedModelWrites = Json.writes[NotifySoundLimitedModel]
}
