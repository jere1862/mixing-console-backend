package TestControllers

import controllers.NodeController
import mocks.AudioNodeMocks
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.JsArray
import play.api.test.Helpers._
import play.api.test._
import play.test.WithApplication
import services.communication.CommunicationService
import services.node.NodeService

class NodeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with MockitoSugar {
  "NodeContoller GET" must {

    "return a list of nodes from the database" in new WithApplication(){
      val mockNodeService = mock[NodeService]
      val mockCommunicationService = mock[CommunicationService]
      when(mockNodeService.list) thenReturn AudioNodeMocks.mocks.values.toSeq

      val controller = new NodeController(mockNodeService, mockCommunicationService)
      controller.setControllerComponents(stubControllerComponents())

      val response = controller.nodes().apply(FakeRequest(GET, "/api/nodes"))

      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
      val content = contentAsJson(response)
      content mustBe a [JsArray]
    }
  }
  // TODO: Fix the test below so that it works with dependency injection
  /*
  "NodeController PUT" must {
    "notify the communication service of a change" in {
      val mockNodeService = mock[NodeService]
      val mockCommunicationService = mock[CommunicationService]
      doNothing().when(mockCommunicationService).notifyNodeSoundChange(_)

      val controller = new NodeController(mockNodeService, mockCommunicationService)
      controller.setControllerComponents(stubControllerComponents())

      val headers: FakeHeaders = FakeHeaders()
      val body = NotifyNodeSoundChangeModel.apply(1, 0, 155)
      val request: FakeRequest[NotifyNodeSoundChangeModel] = FakeRequest.apply(PUT, "/api/nodes/notifyChange",headers, body)

      controller.notifyChange().apply(request)

      verify(mockCommunicationService).notifyNodeSoundChange(ArgumentMatchers.refEq(body))
    }
  }
  */
}
