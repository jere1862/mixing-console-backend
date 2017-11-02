package services.communication

trait CommunicationService {
  def send(id: Int): Unit
}
