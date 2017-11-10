package models

case class MicrophoneDataModel(id: Int, isFix: Boolean, volume: Short, low: Short,
                               med: Short, high: Short) extends DataModel{
}
