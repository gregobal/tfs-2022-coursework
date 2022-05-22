package eventus.endpoint

import eventus.common.AppError.handleServerLogicError
import eventus.common.types.{CommunityId, EventId}
import eventus.dto.{ApiErrorDTO, EventCreateDTO}
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
      .description("Get list of events by community")
      .in(path[UUID]("communityId"))
      .in("events")
      .out(jsonBody[List[Event]])
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(uuid =>
        handleServerLogicError(
          EventService(_.getAllOrByCommunityId(Some(CommunityId(uuid))))
        )
      ),
    communityEndpointRoot.post
      .description("Create event for community")
      .in(path[UUID]("communityId"))
      .in("events")
      .in(jsonBody[EventCreateDTO])
      .out(jsonBody[EventId])
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(p =>
        handleServerLogicError(
          EventService(_.create(CommunityId(p._1), p._2))
        )
      ),
    eventEndpointRoot.get
      .description("Get list of events or filter it by community")
      .in(query[Option[UUID]]("communityId"))
      .out(jsonBody[List[Event]])
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(communityIdOpt =>
        handleServerLogicError(
          EventService(
            _.getAllOrByCommunityId(communityIdOpt.map(CommunityId(_)))
          )
        )
      ),
    eventEndpointRoot.get
      .description("Get event by its id")
      .in(path[UUID]("eventId"))
      .out(jsonBody[Option[Event]])
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(id =>
        handleServerLogicError(
          EventService(_.getById(EventId(id)))
        )
      ),
    eventEndpointRoot.put
      .description("Update existed event")
      .in(jsonBody[Event])
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(eventCreateDTO =>
        handleServerLogicError(
          EventService(_.update(eventCreateDTO))
        )
      )
  )

}
