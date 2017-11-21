package services.communication

import com.google.inject.ImplementedBy
import models.{NotifyAutomaticAdjustmentModel, NotifyNodeSoundChangeModel, NotifySoundLimitedModel}

@ImplementedBy(classOf[UDPCommunicationService])
trait CommunicationService {
  def notifyNodeSoundChange(notifyNodeSoundChangeModel: NotifyNodeSoundChangeModel): Unit
  def notifyAutomaticAdjustment(notifyAutomaticAdjustmentModel: NotifyAutomaticAdjustmentModel): Unit
  def notifySoundLimited(notifySoundLimitedModel: NotifySoundLimitedModel) : Unit
}
