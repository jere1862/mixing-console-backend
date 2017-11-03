package services.communication

import com.google.inject.ImplementedBy

@ImplementedBy(classOf[UDPCommunicationService])
trait CommunicationService {
  def send(message: String): Unit
}
