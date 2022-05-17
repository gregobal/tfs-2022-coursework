package eventus.endpoint

import eventus.dto.ParticipantCreateDTO
import eventus.model.Participant
import eventus.service.ParticipantService
import io.circe.generic.auto._
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}
import sttp.tapir._

object ParticipantEndpoint {

  private val participantEndpoint = endpoint.in("participants")

  // TODO - ошибки временно нсообщением к клиенту как есть, доработать

  val all: List[ZServerEndpoint[ParticipantService, ZioStreams]] = List(

    participantEndpoint.get
      .in(path[String]("id"))
      .out(jsonBody[Option[Participant]])
      .errorOut(jsonBody[String])
      .zServerLogic(id =>
        ParticipantService(_.getById(id))
          .mapError(err => err.message)
      ),

    participantEndpoint.get
      .in(query[String]("eventId").and(query[Option[String]]("memberId")))
      .out(jsonBody[List[Participant]])
      .errorOut(jsonBody[String])
      .zServerLogic(p =>
        ParticipantService(_.getByQueryParams(p._1, p._2))
          .mapError(err => err.message)
      ),

    participantEndpoint.post
      .in(jsonBody[ParticipantCreateDTO])
      .out(jsonBody[String])
      .errorOut(jsonBody[String])
      .zServerLogic(eventCreateDTO =>
        ParticipantService(_.create(eventCreateDTO))
          .mapError(err => err.message)
      ),

    participantEndpoint.delete
      .in(path[String]("id"))
      .errorOut(jsonBody[String])
      .zServerLogic(id =>
        ParticipantService(_.delete(id))
          .mapError(err => err.message)
      )
  )

}
