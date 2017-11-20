package models

case class MicrophoneDataModel(id: Int,
                               isFix: Boolean,
                               volume: Int,
                               low: Int,
                               med: Int,
                               high: Int) extends DataModel{
  def getId = id
}
