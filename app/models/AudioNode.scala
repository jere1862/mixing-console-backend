package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class AudioNode(id: Int, name: String, volume: Short, low: Short,
                     med: Short, high: Short, latitude: Float, longitude: Float,
                     isFix: Boolean, markerOpacity: Float) {
}

object AudioNode {
  implicit val audioNodeReads = Json.reads[AudioNode]
  implicit val nodeWrites = Json.writes[AudioNode]
}
