package eventus.endpoint

import eventus.common.AppError.handleServerLogicError
import eventus.common.types.EventId
import eventus.dto.{ApiErrorDTO, ReviewCreateDTO}
import eventus.endpoint.EventEndpoint.eventEndpointRoot
import eventus.model.Review
import eventus.service.ReviewService
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
        .in(path[UUID]("id"))
        .in("reviews")
        .out(jsonBody[List[Review]])
        .errorOut(jsonBody[ApiErrorDTO])
        .zServerLogic(id =>
          handleServerLogicError(
            ReviewService(_.getByEventId(EventId(id)))
          )
        ),
      eventEndpointRoot.post
        .in(path[UUID]("id"))
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
