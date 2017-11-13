package ocado.scada.tag.stream.model

case class TagUpdate(blockId: String, tagClass: String, value: Int)

object TagUpdate {
  implicit val marshaller = jsonFormat3(TagUpdate.apply)
}
