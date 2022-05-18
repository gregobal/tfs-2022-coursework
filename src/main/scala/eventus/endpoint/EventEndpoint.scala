package eventus.endpoint

import eventus.common.types.{CommunityId, EventId}
import eventus.dto.EventCreateDTO
import eventus.endpoint.CommunityEndpoint.communityEndpointRoot
import eventus.model.Event
import eventus.service.{EventService, MemberService, NotificationService}
import io.circe.generic.auto._
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}
import sttp.tapir.{endpoint, path, query}

import java.util.UUID

object EventEndpoint {

  private[endpoint] val eventEndpointRoot = endpoint.in("events").tag("Event")

  // TODO - ошибки временно нсообщением к клиенту как есть, доработать
  val all: List[ZServerEndpoint[
    MemberService with NotificationService with EventService,
    ZioStreams
  ]] = List(
    communityEndpointRoot.get
      .in(path[UUID]("communityId"))
      .in("events")
      .out(jsonBody[List[Event]])
      .errorOut(jsonBody[String])
      .zServerLogic(uuid =>
        EventService(_.getAllOrByCommunityId(Some(CommunityId(uuid))))
          .mapError(err => err.message)
      ),
    communityEndpointRoot.post
      .in(path[UUID]("communityId"))
      .in("events")
      .in(jsonBody[EventCreateDTO])
      .out(jsonBody[EventId])
      .errorOut(jsonBody[String])
      .zServerLogic(p =>
        EventService(_.create(CommunityId(p._1), p._2))
          .mapError(err => err.message)
      ),
    eventEndpointRoot.get
      .in(query[Option[UUID]]("communityId"))
      .out(jsonBody[List[Event]])
      .errorOut(jsonBody[String])
      .zServerLogic(communityIdOpt =>
        EventService(
          _.getAllOrByCommunityId(communityIdOpt.map(CommunityId(_)))
        )
          .mapError(err => err.message)
      ),
    eventEndpointRoot.get
      .in(path[UUID]("id"))
      .out(jsonBody[Option[Event]])
      .errorOut(jsonBody[String])
      .zServerLogic(id =>
        EventService(_.getById(EventId(id)))
          .mapError(err => err.message)
      ),
    eventEndpointRoot.put
      .in(jsonBody[Event])
      .errorOut(jsonBody[String])
      .zServerLogic(eventCreateDTO =>
        EventService(_.update(eventCreateDTO))
          .mapError(err => err.message)
      )
  )

}
