package services.node

import models.AudioNode

import scala.collection.mutable.Map

class DBNodeService() extends NodeService {
  override def list:Seq[AudioNode] = {
    DBNodeService.db.values.toSeq.filter(node => node.hasMicAndDataSet)
  }

  override def get(id: Int): Option[AudioNode] = {
    return DBNodeService.db.get(id)
  }

  override def save(audioNode:AudioNode): Unit = {
    DBNodeService.db(audioNode.id) = audioNode
  }

  override def reset(): Unit = {
    DBNodeService.db.clear
    println("Clearing database.")
  }

}

object DBNodeService {
  private val db = Map.empty[Int, AudioNode]
}
