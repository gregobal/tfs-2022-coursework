package eventus.endpoint

import eventus.dto.MemberCreateDTO
import eventus.model.Member
import eventus.service.MemberService
import io.circe.generic.auto._
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}
import sttp.tapir.{endpoint, path, query}

object MemberEndpoint {

  private val memberEndpoint = endpoint.in("members")

  // TODO - ошибки временно нсообщением к клиенту как есть, доработать

  val all: List[ZServerEndpoint[MemberService, ZioStreams]] = List(
    memberEndpoint.get
      .in(query[String]("communityId"))
      .out(jsonBody[List[Member]])
      .errorOut(jsonBody[String])
      .zServerLogic(communityId =>
        MemberService(_.getByCommunityId(communityId))
          .mapError(err => err.message)
      ),

    memberEndpoint.get
      .in(path[String]("id"))
      .out(jsonBody[Option[Member]])
      .errorOut(jsonBody[String])
      .zServerLogic(id =>
        MemberService(_.getById(id))
          .mapError(err => err.message)
      ),

    memberEndpoint.post
      .in(jsonBody[MemberCreateDTO])
      .out(jsonBody[String])
      .errorOut(jsonBody[String])
      .zServerLogic(eventCreateDTO =>
        MemberService(_.create(eventCreateDTO))
          .mapError(err => err.message)
      ),

    memberEndpoint.delete
      .in(path[String]("id"))
      .errorOut(jsonBody[String])
      .zServerLogic(id =>
        MemberService(_.delete(id))
          .mapError(err => err.message)
      )
  )

}
