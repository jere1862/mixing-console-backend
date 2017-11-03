package controllers

import javax.inject._

import models.{AudioNode, NotifyChangeModel}
import play.api._
import play.api.mvc._
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.json._
import services.node.NodeService
import services.communication.CommunicationService

@Singleton
class NodeController @Inject()(nodeService: NodeService, communicationService: CommunicationService) extends InjectedController() {
  def addNode() = Action { implicit request: Request[AnyContent] =>
    parseRequest(request, (audioNode: AudioNode) => {
      Logger.info("Creating or updating node " + audioNode.id)
      nodeService.save(audioNode)
    })
    Ok.withHeaders(
      ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
      ACCESS_CONTROL_ALLOW_HEADERS -> "*",
      ACCESS_CONTROL_ALLOW_METHODS -> "GET"
    )
  }

  def nodes() = Action { implicit request: Request[AnyContent] =>
    def nodeList = nodeService.list
    Ok(Json.toJson(nodeList)).withHeaders(
      ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
      ACCESS_CONTROL_ALLOW_HEADERS -> "*",
      ACCESS_CONTROL_ALLOW_METHODS -> "PUT"
    )
  }

  def notifyChange(id: Int) = Action { implicit request: Request[AnyContent] =>
    parseRequest(request, (notifyChangeModel: NotifyChangeModel) => {
      Logger.info("Sending update to node " + id)
      // Todo: modify the call so it makes sense
      communicationService.send(id.toString)
    })
    Ok
  }

  def parseRequest[A](request: Request[AnyContent], callback: (A) => Unit )(implicit reqReads: Reads[A]): Unit ={
    val json = request.body.asJson.get
    val resultObject: JsResult[A] = Json.fromJson[A](json)

    resultObject match {
      case c: JsSuccess[A] => {
        val obj: A = c.get
        callback(obj)
      }
      case e: JsError => {
        Logger.info("Error parsing json.")
        Logger.info(e.toString)
      }
    }
  }
}
