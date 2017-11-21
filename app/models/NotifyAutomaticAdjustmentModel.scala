package models

import play.api.libs.json.Json

case class NotifyAutomaticAdjustmentModel(id: Int, adjustAutomatically: Boolean) {
}

object NotifyAutomaticAdjustmentModel {
  implicit val notifyAutomaticAdjustmentModelReads = Json.reads[NotifyAutomaticAdjustmentModel]
  implicit val notifyAutomaticAdjustmentModelWrites = Json.writes[NotifyAutomaticAdjustmentModel]
}
