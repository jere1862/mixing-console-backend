package models

case class MicrophoneGainDataModel(id: Int,
                              isFix: Boolean,
                              volumeSlider: Int,
                              lowSlider: Int,
                              medSlider:  Int,
                              highSlider: Int) extends DataModel{
    def getId = id
}
