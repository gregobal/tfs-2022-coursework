package eventus.endpoint

import eventus.dto.EventCreateDTO
import eventus.model.Event
import eventus.service.EventService
import io.circe.generic.auto._
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}
import sttp.tapir.{endpoint, path, query}

object EventEndpoint {

  private val eventEndpoint = endpoint.in("events")

  // TODO - ошибки временно нсообщением к клиенту как есть, доработать

  val all: List[ZServerEndpoint[EventService, ZioStreams]] = List(
    eventEndpoint.get
      .in(query[String]("communityId"))
      .out(jsonBody[List[Event]])
      .errorOut(jsonBody[String])
      .zServerLogic(communityId =>
        EventService(_.getByCommunityId(communityId))
          .mapError(err => err.message)
      ),
    eventEndpoint.get
      .in(path[String]("id"))
      .out(jsonBody[Option[Event]])
      .errorOut(jsonBody[String])
      .zServerLogic(id =>
        EventService(_.getById(id))
          .mapError(err => err.message)
      ),
    eventEndpoint.post
      .in(jsonBody[EventCreateDTO])
      .out(jsonBody[String])
      .errorOut(jsonBody[String])
      .zServerLogic(eventCreateDTO =>
        EventService(_.create(eventCreateDTO))
          .mapError(err => err.message)
      ),
    eventEndpoint.put
      .in(jsonBody[Event])
      .errorOut(jsonBody[String])
      .zServerLogic(eventCreateDTO =>
        EventService(_.update(eventCreateDTO))
          .mapError(err => err.message)
      )
  )

}
