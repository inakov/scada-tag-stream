package ocado.scada.tag.stream

import java.util.Properties

import ocado.scada.tag.stream.model.{BlockDefinition, TagDefinition, TagUpdate}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import spray.json._

import scala.annotation.tailrec

object TagStreamDriver extends App {
  val blockId = "Test_Block_"

  val tags = List("BRM_PSM_Count",
    "NoRead_Count",
    "TPM_Count",
    "ATL_Count",
    "Disabled_Count",
    "DestError_Count",
    "Full_Count",
    "Mistrack_Count",
    "Diverter_Error_Count",
    "Dest_Occupied_Count",
    "Unexpected_Count",
    "Reset_Statistics",
    "Enable_Live_Stats",
    "Feedback_Fault",
    "Jam",
    "Comms_Fault",
    "Motor_1_in_Error",
    "Motor_2_in_Error",
    "Motor_3_in_Error",
    "Spare",
    "Feedback_Fault",
    "Jam",
    "Comms_Fault",
    "Motor_1_in_Error",
    "Motor_2_in_Error",
    "Motor_3_in_Error",
    "Spare",
    "ErrorCode",
    "Overload_Error",
    "Switched_Off",
    "Slip_Detection_Fault",
    "Config_Error",
    "Spare4",
    "Cons_Left_Missed",
    "Cons_Straight_Missed",
    "Cons_Right_Missed",
    "Spare1",
    "Spare2",
    "Spare3",
    "Spare4",
    "Spare5",
    "Timeout_Fault",
    "Dest_Occupied",
    "Module_Default_Fault",
    "Start_While_Busy_Fault",
    "Module_Not_Healthy",
    "Start_Too_Soon",
    "Cons_NoReads",
    "Trigger_PEC_Jam",
    "Feedback_Fault",
    "Feedback_Fault_Deact",
    "Transfer_in_Manual",
    "Too_Many_Mistracks",
    "Ok",
    "ByteField",
    "IntField",
    "DintField",
    "CharField",
    "IntField1",
    "CharField1",
    "DintField11",
    "CharField11",
    "CharField12",
    "ByteField1",
    "DintField1",
    "Ok1",
    "ByteField2",
    "DintField2",
    "ByteField21",
    "DintField12",
    "CharField13",
    "IntField2",
    "ByteField3",
    "DintField13",
    "Ok11",
    "CharField2",
    "DintField131",
    "Full",
    "HalfFull",
    "Energy_Save",
    "Infeed_Allowed",
    "Occupied",
    "ManMode_Act",
    "Disabled",
    "Current_Strt_WaitTime",
    "Current_Rght_WaitTime",
    "Current_Default_Dest",
    "LeftDest_Disabled",
    "StraightDest_Disabled",
    "RightDest_Disabled",
    "Label_Direction",
    "Disabled",
    "Man_Mode_Active",
    "Activated",
    "ManMode_Act")




  def generateBlocks(size: Int) = {
    (1 to size).map(blockId + _).map((_, drawTags(tags))).map{ r =>
      val (block, tags) = r
      BlockDefinition(block, tags.map(tagClass => TagDefinition(block, tagClass)))
    }
  }

  def drawTags(tags: List[String]): List[String] = {
    val randomGen = new scala.util.Random()

    @tailrec
    def next(acc: List[String]): List[String] = {
      val tag = tags(randomGen.nextInt(tags.size))
      val result = tag :: acc

      if(randomGen.nextInt(100) >= 30) next(result)
      else result
    }

    next(Nil)
  }


  val blocks = generateBlocks(100000)

  val randomGen = new scala.util.Random()
  val tagUpdates = blocks.flatMap(block => block.tags.map(tag => TagUpdate(tag.blockId, tag.tagClass, randomGen.nextInt())))

  val blockDefinitionsTopic = "block_definitions"
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("acks", "1")
  props.put("retries", "3")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)
//  blocks.map(block => new ProducerRecord[String, String]("block_definitions", block.id, block.toJson.toString)).foreach(producer.send)

  tagUpdates.map(tag => new ProducerRecord[String, String]("tag_updates", tag.blockId, tag.toJson.toString)).foreach(producer.send)
//  val update = new ProducerRecord[String, String]("tag_updates", "Test_Block_1049", TagUpdate("Test_Block_1049", "Motor_3_in_Error", 0).toJson.toString)
//  producer.send(update)
  producer.close()

}
