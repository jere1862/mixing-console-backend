package services.communication

import com.google.inject.ImplementedBy
import models.NotifyNodeSoundChangeModel

@ImplementedBy(classOf[UDPCommunicationService])
trait CommunicationService {
  def notifyNodeSoundChange(id: Int, notifyNodeSoundChangeModel: NotifyNodeSoundChangeModel): Unit
  def notifyAutomaticAdjustment(adjustAutomatically: Boolean): Unit
}
