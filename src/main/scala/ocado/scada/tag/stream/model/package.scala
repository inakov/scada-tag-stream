package ocado.scada.tag.stream

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

package object model extends SprayJsonSupport with DefaultJsonProtocol {

  implicit def implicitSprayJsonMarshaller[T](implicit writer: RootJsonWriter[T], printer: JsonPrinter = PrettyPrinter): ToEntityMarshaller[T] = sprayJsonMarshaller
  implicit def implicitSprayJsonUnmarshaller[T: RootJsonReader]: FromEntityUnmarshaller[T] = sprayJsonUnmarshaller

}
