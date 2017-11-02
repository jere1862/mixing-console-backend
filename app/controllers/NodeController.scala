package controllers

import javax.inject._

import models.{AudioNode, NotifyChangeModel}
import play.api._
import play.api.mvc._
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.json._
import services.node.NodeService

@Singleton
class NodeController @Inject()(nodeService: NodeService) extends InjectedController() {
  def nodes() = Action { implicit request: Request[AnyContent] =>
    def nodeList = nodeService.list
    implicit val nodeWrites = Json.writes[AudioNode]
    Ok(Json.toJson(nodeList))
  }

  def notifyChange(id: Int) = Action { implicit request: Request[AnyContent] =>
    val json = request.body.asJson.get
    implicit val notifyChangeModelReads = Json.reads[NotifyChangeModel]
    val resultObject: JsResult[NotifyChangeModel] = Json.fromJson[NotifyChangeModel](json)

    resultObject match {
      case c: JsSuccess[NotifyChangeModel] => {
        val notifyChangeModel: NotifyChangeModel = c.get
        Logger.info("Sending update to node " + id)
      }
      case e: JsError => {
        Logger.info("Error parsing Stripe charge: ")
      }
    }
    Ok
  }
}
