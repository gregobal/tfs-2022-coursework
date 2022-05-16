package eventus.endpoint

import eventus.dto.EventCreateDTO
import eventus.model.Event
import eventus.service.EventService
import sttp.tapir.endpoint
import sttp.tapir.generic.auto._
import io.circe.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.ztapir._

object EventEndpoint {

  private val eventEndpoint = endpoint.in("events")

  // TODO - ошибки временно нсообщением к клиенту как есть, доработать
  val routes = List(
    eventEndpoint.get
      .out(jsonBody[List[Event]])
      .errorOut(jsonBody[String])
      .zServerLogic[EventService](_ => EventService(_.getAll).mapError(err => err.message)),

    eventEndpoint.get
      .in(path[String]("id"))
      .out(jsonBody[Option[Event]])
      .errorOut(jsonBody[String])
      .zServerLogic[EventService](id => EventService(_.getById(id)).mapError(err => err.message)),

    eventEndpoint.post
      .in(jsonBody[EventCreateDTO])
      .out(jsonBody[String])
      .errorOut(jsonBody[String])
      .zServerLogic[EventService](eventCreateDTO => EventService(_.create(eventCreateDTO)).mapError(err => err.message)),

    eventEndpoint.put
      .in(jsonBody[Event])
      .errorOut(jsonBody[String])
      .zServerLogic[EventService](eventCreateDTO => EventService(_.update(eventCreateDTO)).mapError(err => err.message))
  )

}
