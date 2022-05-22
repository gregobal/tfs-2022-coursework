package eventus.community.service

import eventus.common.AppError
import eventus.common.types.{CommunityId, MemberId}
import eventus.community.dto.{MemberCreateDTO, MemberIsNotifyDTO}
import eventus.community.model.Member
import zio.{Accessible, IO}

trait MemberService {
  def getByCommunityId(communityId: CommunityId): IO[AppError, List[Member]]
  def getById(id: MemberId): IO[AppError, Option[Member]]
  def create(
      communityId: CommunityId,
      memberCreateDTO: MemberCreateDTO
  ): IO[AppError, MemberId]
  def delete(id: MemberId): IO[AppError, Unit]
  def setNotify(memberIsNotifyDTO: MemberIsNotifyDTO): IO[AppError, Unit]
}

object MemberService extends Accessible[MemberService]
