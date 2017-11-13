package ocado.scada.tag.stream.model

import spray.json.{DeserializationException, JsObject, JsString, JsValue, RootJsonFormat}

case class TagDefinition(blockId: String, tagClass: String){
  val tagId: String = s"$blockId:$tagClass"
}

object TagDefinition {
  implicit object tagJsonFormat extends RootJsonFormat[TagDefinition] {
    def write(t: TagDefinition) = JsObject(
      "blockId" -> JsString(t.blockId),
      "tagClass" -> JsString(t.tagClass)
    )

    def read(value: JsValue) = {
      value.asJsObject.getFields("blockId", "tagClass") match {
        case Seq(JsString(blockId), JsString(tagClass)) => TagDefinition(blockId, tagClass)
        case _ => throw new DeserializationException("TagDefinition expected")
      }
    }
  }
}
