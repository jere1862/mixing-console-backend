package models

case class MicrophoneVolumeDataModel(id: Int,
                                isFix: Boolean,
                                volumeSlider: Int
                               ) extends DataModel
{
  override def getId(): Int = id
}
