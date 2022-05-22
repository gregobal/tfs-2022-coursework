package eventus.event.endpoint

import eventus.common.AppError.handleServerLogicError
import eventus.common.dto.ApiErrorDTO
import eventus.common.types.EventId
import eventus.event.dto.ReviewCreateDTO
import eventus.event.endpoint.EventEndpoint.eventEndpointRoot
import eventus.event.model.Review
import eventus.event.service.ReviewService
import io.circe.generic.auto._
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.path
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}

import java.util.UUID

object ReviewEndpoint {

  val all: List[ZServerEndpoint[ReviewService, ZioStreams]] =
    List(
      eventEndpointRoot.get
        .description("Get reviews by event")
        .in(path[UUID]("eventId"))
        .in("reviews")
        .out(jsonBody[List[Review]])
        .errorOut(jsonBody[ApiErrorDTO])
        .zServerLogic(id =>
          handleServerLogicError(
            ReviewService(_.getByEventId(EventId(id)))
          )
        ),
      eventEndpointRoot.post
        .description("Create review on event")
        .in(path[UUID]("eventId"))
        .in("review")
        .in(jsonBody[ReviewCreateDTO])
        .errorOut(jsonBody[ApiErrorDTO])
        .zServerLogic(p =>
          handleServerLogicError(
            ReviewService(_.add(EventId(p._1), p._2))
          )
        )
    )
}
