package eventus.endpoint

import eventus.common.AppError.handleServerLogicError
import eventus.common.types.{EventId, MemberId}
import eventus.dto.ApiErrorDTO
import eventus.endpoint.EventEndpoint.eventEndpointRoot
import eventus.model.Participant
import eventus.service.ParticipantService
import io.circe.generic.auto._
import sttp.capabilities.zio.ZioStreams
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}

import java.util.UUID

object ParticipantEndpoint {

  private val eventId = path[UUID]("eventId")
  private val memberId = path[UUID]("memberId")

  val all: List[ZServerEndpoint[ParticipantService, ZioStreams]] = List(
    eventEndpointRoot.get
      .in(eventId)
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
      .in(eventId)
      .in("register")
      .in(memberId)
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(p =>
        handleServerLogicError(
          ParticipantService(_.register(EventId(p._1), MemberId(p._2)))
        )
      ),
    eventEndpointRoot.delete
      .in(path[UUID]("eventId"))
      .in("unregister")
      .in(memberId)
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(p =>
        handleServerLogicError(
          ParticipantService(_.unregister(EventId(p._1), MemberId(p._2)))
        )
      )
  )

}
