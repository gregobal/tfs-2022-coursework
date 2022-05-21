package eventus.endpoint

import eventus.common.AppError.handleServerLogicError
import eventus.common.types.CommunityId
import eventus.dto.CommunityCreateDTO
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
      .out(jsonBody[List[Community]])
      .errorOut(jsonBody[String])
      .zServerLogic(_ =>
        handleServerLogicError(
          CommunityService(_.getAll)
        )
      ),
    communityEndpointRoot.get
      .in(path[UUID]("id"))
      .out(jsonBody[Option[Community]])
      .errorOut(jsonBody[String])
      .zServerLogic(id =>
        handleServerLogicError(
          CommunityService(_.getById(CommunityId(id)))
        )
      ),
    communityEndpointRoot.post
      .in(jsonBody[CommunityCreateDTO])
      .out(jsonBody[CommunityId])
      .errorOut(jsonBody[String])
      .zServerLogic(communityCreateDTO =>
        handleServerLogicError(
          CommunityService(_.create(communityCreateDTO))
        )
      ),
    communityEndpointRoot.put
      .in(jsonBody[Community])
      .errorOut(jsonBody[String])
      .zServerLogic(community =>
        handleServerLogicError(
          CommunityService(_.update(community))
        )
      ),
    communityEndpointRoot.get
      .in("search")
      .in(query[String]("q"))
      .out(jsonBody[List[Community]])
      .errorOut(jsonBody[String])
      .zServerLogic(q =>
        handleServerLogicError(
          CommunityService(_.search(q))
        )
      )
  )
}
