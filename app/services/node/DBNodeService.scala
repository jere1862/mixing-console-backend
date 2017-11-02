package services.node

import models.AudioNode

import scala.collection.mutable.Map

class DBNodeService() extends NodeService {
  override def list:Seq[AudioNode] = {
    db.values.toSeq
  }

  private val db = Map(
    1 -> AudioNode(1, "mic 1", 95, 12, 40, 50, 45.3701f, -71.9269f, false, 0.5f),
    2 -> AudioNode(2, "mic 1", 50, 7, 20, 50, 45.3701f, -71.9269f, false, 0.5f)
  )

  override def get(id: Int): Option[AudioNode] = {
    return null;
  }

  override def save(audioNode:AudioNode): Unit = {
    db(audioNode.id) = audioNode
  }
}