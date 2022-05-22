package eventus.endpoint

import eventus.common.AppError.handleServerLogicError
import eventus.common.types.CommunityId
import eventus.dto.{ApiErrorDTO, CommunityCreateDTO}
import eventus.model.Community
import eventus.service.CommunityService
import io.circe.generic.auto._
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}
import sttp.tapir.{endpoint, path, query}

import java.util.UUID

object CommunityEndpoint {

  private[endpoint] val communityEndpointRoot =
    endpoint.in("communities").tag("Community")

  val all: List[ZServerEndpoint[CommunityService, ZioStreams]] = List(
    communityEndpointRoot.get
      .description("Get list of communities")
      .out(jsonBody[List[Community]])
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(_ =>
        handleServerLogicError(
          CommunityService(_.getAll)
        )
      ),
    communityEndpointRoot.get
      .description("Get community by its id")
      .in(path[UUID]("communityId"))
      .out(jsonBody[Option[Community]])
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(id =>
        handleServerLogicError(
          CommunityService(_.getById(CommunityId(id)))
        )
      ),
    communityEndpointRoot.post
      .description("Create new community")
      .in(jsonBody[CommunityCreateDTO])
      .out(jsonBody[CommunityId])
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(communityCreateDTO =>
        handleServerLogicError(
          CommunityService(_.create(communityCreateDTO))
        )
      ),
    communityEndpointRoot.put
      .description("Update existing community")
      .in(jsonBody[Community])
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(community =>
        handleServerLogicError(
          CommunityService(_.update(community))
        )
      ),
    communityEndpointRoot.get
      .description("Search in communities by keywords")
      .in("search")
      .in(query[String]("q"))
      .out(jsonBody[List[Community]])
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(q =>
        handleServerLogicError(
          CommunityService(_.search(q))
        )
      )
  )
}
