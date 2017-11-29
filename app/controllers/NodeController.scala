package controllers

import javax.inject._

import models.{AudioNode, NotifyAutomaticAdjustmentModel, NotifyNodeSoundChangeModel, NotifySoundLimitedModel}
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
    Ok
  }

  def nodes() = Action { implicit request: Request[AnyContent] =>
    def nodeList = nodeService.list
    Ok(Json.toJson(nodeList))
  }

  def notifyChange() = Action { implicit request: Request[AnyContent] =>
    parseRequest(request, (notifyChangeModel: NotifyNodeSoundChangeModel) => {
      communicationService.notifyNodeSoundChange(notifyChangeModel)
    })
    Ok
  }

  def notifyAutomaticAdjustment() = Action { implicit request: Request[AnyContent] =>
    parseRequest(request, (notifyAutomaticAdjustmentModel: NotifyAutomaticAdjustmentModel) => {
      communicationService.notifyAutomaticAdjustment(notifyAutomaticAdjustmentModel)
    })
    Ok
  }

  def notifySoundLimited() = Action { implicit request: Request[AnyContent] =>
    parseRequest(request, (notifySoundLimitedModel: NotifySoundLimitedModel) => {
    communicationService.notifySoundLimited(notifySoundLimitedModel)
  })
    Ok
  }

  def reset() = Action { implicit request: Request[AnyContent] =>
    nodeService.reset
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
