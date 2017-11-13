package ocado.scada.tag.stream.model

case class BlockDefinition(id: String, tags: List[TagDefinition])

object BlockDefinition {
  implicit val marshaller = jsonFormat2(BlockDefinition.apply)
}
