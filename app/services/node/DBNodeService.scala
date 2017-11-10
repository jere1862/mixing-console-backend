package services.node

import models.AudioNode

import scala.collection.mutable.Map

class DBNodeService() extends NodeService {
  override def list:Seq[AudioNode] = {
    db.values.toSeq
  }

  private val db = Map(
    1 -> AudioNode(1, "mic test", 10, 12, 40, 50, 45.37801f, -71.9269f, false, 1f),
    2 -> AudioNode(2, "mic TEST", 50, 60, 60, 50, 45.37848f, -71.92771f, false, 0.6f),
    3 -> AudioNode(3, "mic 3", 50, 22, 20, 50, 45.378246f,  -71.92804f, false, 0.6f),
    4 -> AudioNode(4, "fix mic", 50, 76, 15, 50, 45.378246f, -71.92742f, true, 0.6f)
  )

  override def get(id: Int): Option[AudioNode] = {
    return null;
  }

  override def save(audioNode:AudioNode): Unit = {
    db(audioNode.id) = audioNode
  }
}