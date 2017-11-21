package services.node

import com.google.inject.ImplementedBy
import models.AudioNode

@ImplementedBy(classOf[DBNodeService])
trait NodeService {
  def list: Seq[AudioNode]
  def get(id: Int): Option[AudioNode]
  def save(audioNode: AudioNode): Unit
  def reset(): Unit
}