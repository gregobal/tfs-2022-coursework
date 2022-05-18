package eventus.endpoint

import eventus.common.types.{CommunityId, MemberId}
import eventus.dto.MemberCreateDTO
import eventus.endpoint.CommunityEndpoint.communityEndpointRoot
import eventus.model.Member
import eventus.service.MemberService
import io.circe.generic.auto._
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}
import sttp.tapir.{endpoint, path}

import java.util.UUID

object MemberEndpoint {

  private val memberEndpointRoot = endpoint.in("members").tag("Community")

  // TODO - ошибки временно нсообщением к клиенту как есть, доработать
  val all: List[ZServerEndpoint[MemberService, ZioStreams]] = List(
    communityEndpointRoot.get
      .in(path[UUID]("communityId"))
      .in("members")
      .out(jsonBody[List[Member]])
      .errorOut(jsonBody[String])
      .zServerLogic(uuid =>
        MemberService(_.getByCommunityId(CommunityId(uuid)))
          .mapError(err => err.message)
      ),
    communityEndpointRoot.post
      .in(path[UUID]("communityId"))
      .in("members")
      .in(jsonBody[MemberCreateDTO])
      .out(jsonBody[MemberId])
      .errorOut(jsonBody[String])
      .zServerLogic(p =>
        MemberService(_.create(CommunityId(p._1), p._2))
          .mapError(err => err.message)
      ),
    memberEndpointRoot.get
      .in(path[UUID]("id"))
      .out(jsonBody[Option[Member]])
      .errorOut(jsonBody[String])
      .zServerLogic(id =>
        MemberService(_.getById(MemberId(id)))
          .mapError(err => err.message)
      ),
    memberEndpointRoot.delete
      .in(path[UUID]("id"))
      .errorOut(jsonBody[String])
      .zServerLogic(id =>
        MemberService(_.delete(MemberId(id)))
          .mapError(err => err.message)
      )
  )
}
