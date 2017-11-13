package ocado.scada.tag.stream.health

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._


trait HealthService {

  val healthRoute = path("health") {
    get {
      complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "healthy"))
    }
  }

}
