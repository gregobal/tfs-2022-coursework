package eventus.endpoint

import eventus.common.AppError.handleServerLogicError
import eventus.common.types.{CommunityId, MemberId}
import eventus.dto.{ApiErrorDTO, MemberCreateDTO, MemberIsNotifyDTO}
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

  val all: List[ZServerEndpoint[MemberService, ZioStreams]] = List(
    communityEndpointRoot.get
      .in(path[UUID]("communityId"))
      .in("members")
      .out(jsonBody[List[Member]])
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(uuid =>
        handleServerLogicError(
          MemberService(_.getByCommunityId(CommunityId(uuid)))
        )
      ),
    communityEndpointRoot.post
      .in(path[UUID]("communityId"))
      .in("join")
      .in(jsonBody[MemberCreateDTO])
      .out(jsonBody[MemberId])
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(p =>
        handleServerLogicError(
          MemberService(_.create(CommunityId(p._1), p._2))
        )
      ),
    memberEndpointRoot.get
      .in(path[UUID]("id"))
      .out(jsonBody[Option[Member]])
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(id =>
        handleServerLogicError(
          MemberService(_.getById(MemberId(id)))
        )
      ),
    memberEndpointRoot.put
      .in("notify")
      .in(jsonBody[MemberIsNotifyDTO])
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(p =>
        handleServerLogicError(
          MemberService(_.setNotify(p))
        )
      ),
    memberEndpointRoot.delete
      .in(path[UUID]("id"))
      .errorOut(jsonBody[ApiErrorDTO])
      .zServerLogic(id =>
        handleServerLogicError(
          MemberService(_.delete(MemberId(id)))
        )
      )
  )
}
