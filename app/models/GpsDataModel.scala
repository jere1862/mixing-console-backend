package models

case class GpsDataModel(id: Int, latitude: Float, longitude: Float) extends DataModel{
  def getId = id
}
