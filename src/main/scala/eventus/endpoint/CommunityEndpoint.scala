package eventus.endpoint

import eventus.dto.CommunityCreateDTO
import eventus.model.Community
import eventus.service.CommunityService
import sttp.tapir.{endpoint, path}
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.generic.auto._
import io.circe.generic.auto._
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}

object CommunityEndpoint {

  private val communityEndpoint = endpoint.in("communities")

  // TODO - ошибки временно нсообщением к клиенту как есть, доработать
  val all: List[ZServerEndpoint[CommunityService, ZioStreams]] = List(
    communityEndpoint.get
      .out(jsonBody[List[Community]])
      .errorOut(jsonBody[String])
      .zServerLogic(_ => CommunityService(_.getAll)
        .mapError(err => err.message)
      ),

    communityEndpoint.get
      .in(path[String]("id"))
      .out(jsonBody[Option[Community]])
      .errorOut(jsonBody[String])
      .zServerLogic(id => CommunityService(_.getById(id))
        .mapError(err => err.message)
      ),

    communityEndpoint.post
      .in(jsonBody[CommunityCreateDTO])
      .out(jsonBody[String])
      .errorOut(jsonBody[String])
      .zServerLogic(communityCreateDTO => CommunityService(_.create(communityCreateDTO))
        .mapError(err => err.message)
      ),

    communityEndpoint.put
      .in(jsonBody[Community])
      .errorOut(jsonBody[String])
      .zServerLogic(community => CommunityService(_.update(community))
        .mapError(err => err.message)
      )
  )


}
