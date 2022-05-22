package eventus.event.endpoint

import eventus.common.AppError.handleServerLogicError
import eventus.common.dto.ApiErrorDTO
import eventus.common.types.{EventId, MemberId}
import eventus.event.endpoint.EventEndpoint.eventEndpointRoot
import eventus.event.model.Participant
import eventus.event.service.ParticipantService
import io.circe.generic.auto._
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}
import sttp.tapir.{path, query}

import java.util.UUID

object ParticipantEndpoint {

  val all: List[ZServerEndpoint[ParticipantService, ZioStreams]] = List(
    eventEndpointRoot.get
      .description(
        "Get list of event's participant anf filter its by community member"
      )
      .in(path[UUID]("eventId"))
      .in("participants")
      .in(query[Option[UUID]]("memberId"))
      .out(jsonBody[List[Participant]])
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(p =>
        handleServerLogicError(
          ParticipantService(
            _.getByEventIdAndFilterByMemberId(
              EventId(p._1),
              p._2.map(MemberId(_))
            )
          )
        )
      ),
    eventEndpointRoot.post
      .description("Register on event (become a participant)")
      .in(path[UUID]("eventId"))
      .in("register")
      .in(path[UUID]("memberId"))
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(p =>
        handleServerLogicError(
          ParticipantService(_.register(EventId(p._1), MemberId(p._2)))
        )
      ),
    eventEndpointRoot.delete
      .description("Unregister from event")
      .in(path[UUID]("eventId"))
      .in("unregister")
      .in(path[UUID]("memberId"))
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(p =>
        handleServerLogicError(
          ParticipantService(_.unregister(EventId(p._1), MemberId(p._2)))
        )
      )
  )

}
