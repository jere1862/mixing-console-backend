package models

import play.api.libs.json.Json

case class NotifyAutomaticAdjustmentModel(id: Int, adjustAutomatically: Boolean) {
}

object NotifyAutomaticAdjustmentModel {
  implicit val NotifyAutomaticAdjustmentModelReads = Json.reads[NotifyAutomaticAdjustmentModel]
}
