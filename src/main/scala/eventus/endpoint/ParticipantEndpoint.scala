package eventus.endpoint

import eventus.endpoint.EventEndpoint.eventEndpointRoot
import eventus.model.Participant
import eventus.common.types.{EventId, MemberId}
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
  private val memberId = path[UUID]("eventId")

  // TODO - ошибки временно нсообщением к клиенту как есть, доработать
  val all: List[ZServerEndpoint[ParticipantService, ZioStreams]] = List(
    eventEndpointRoot.get
      .in(eventId)
      .in("participants")
      .in(query[Option[UUID]]("memberId"))
      .out(jsonBody[List[Participant]])
      .errorOut(jsonBody[String])
      .zServerLogic(p =>
        ParticipantService(
          _.getByEventIdAndFilterByMemberId(
            EventId(p._1),
            p._2.map(MemberId(_))
          )
        )
          .mapError(err => err.message)
      ),
    eventEndpointRoot.post
      .in(eventId)
      .in("register")
      .in(memberId)
      .errorOut(jsonBody[String])
      .zServerLogic(p =>
        ParticipantService(_.create(EventId(p._1), MemberId(p._2)))
          .mapError(err => err.message)
      ),
    eventEndpointRoot.delete
      .in(path[UUID]("eventId"))
      .in("unregister")
      .in(memberId)
      .errorOut(jsonBody[String])
      .zServerLogic(p =>
        ParticipantService(_.delete(EventId(p._1), MemberId(p._2)))
          .mapError(err => err.message)
      )
  )

}
