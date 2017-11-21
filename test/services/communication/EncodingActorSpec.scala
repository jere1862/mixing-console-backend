package services.communication

import java.nio.ByteBuffer

import akka.testkit.{TestProbe}
import models.{NotifyAutomaticAdjustmentModel, NotifyNodeSoundChangeModel, NotifySoundLimitedModel}

class EncodingActorSpec extends BaseActorSpec{

  val prob1 = TestProbe()
  val encodingActor = system.actorOf(EncodingActor.props(prob1.ref))

  "An encoding actor" should {

    "encode a node sound change message" in {
      val id: Int = 1
      val sliderType: Int= 2
      val sliderValue: Int = 195
      val notifyNodeSoundChangeModel = NotifyNodeSoundChangeModel(id, sliderType, sliderValue)

      encodingActor ! notifyNodeSoundChangeModel

      val byteBuf = prob1.expectMsgClass(classOf[ByteBuffer])
      byteBuf.position(0)
      byteBuf.get should be (id.toByte)
      byteBuf.get should be (sliderType.toByte)
      byteBuf.get should be (sliderValue.toByte)
    }

    "encode an adjustAutomatically message" in {
      val id: Int = 1
      val adjustAutomatically = 1
      val notifyAdjustAutomatically = NotifyAutomaticAdjustmentModel(id, adjustAutomatically == 1)

      encodingActor ! notifyAdjustAutomatically

      val byteBuf = prob1.expectMsgClass(classOf[ByteBuffer])
      byteBuf.position(0)
      byteBuf.get should be (id.toByte)
      byteBuf.get should be (adjustAutomatically.toByte)
    }

    "encode a soundLimited message " in {
      val limitSound = 1
      val notifyLimitSound = NotifySoundLimitedModel(limitSound == 1)

      encodingActor ! notifyLimitSound

      val byteBuf = prob1.expectMsgClass(classOf[ByteBuffer])
      byteBuf.position(0)
      byteBuf.get should be (limitSound.toByte)
    }
  }
}
