package eventus.endpoint

import eventus.common.AppError.handleServerLogicError
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
        handleServerLogicError(
          EventService(_.getAllOrByCommunityId(Some(CommunityId(uuid))))
        )
      ),
    communityEndpointRoot.post
      .in(path[UUID]("communityId"))
      .in("events")
      .in(jsonBody[EventCreateDTO])
      .out(jsonBody[EventId])
      .errorOut(jsonBody[String])
      .zServerLogic(p =>
        handleServerLogicError(
          EventService(_.create(CommunityId(p._1), p._2))
        )
      ),
    eventEndpointRoot.get
      .in(query[Option[UUID]]("communityId"))
      .out(jsonBody[List[Event]])
      .errorOut(jsonBody[String])
      .zServerLogic(communityIdOpt =>
        handleServerLogicError(
          EventService(
            _.getAllOrByCommunityId(communityIdOpt.map(CommunityId(_)))
          )
        )
      ),
    eventEndpointRoot.get
      .in(path[UUID]("id"))
      .out(jsonBody[Option[Event]])
      .errorOut(jsonBody[String])
      .zServerLogic(id =>
        handleServerLogicError(
          EventService(_.getById(EventId(id)))
        )
      ),
    eventEndpointRoot.put
      .in(jsonBody[Event])
      .errorOut(jsonBody[String])
      .zServerLogic(eventCreateDTO =>
        handleServerLogicError(
          EventService(_.update(eventCreateDTO))
        )
      )
  )

}
