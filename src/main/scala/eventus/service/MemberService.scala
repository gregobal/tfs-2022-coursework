package eventus.service

import eventus.dto.MemberCreateDTO
import eventus.error.AppError
import eventus.model.Member
import zio.{Accessible, IO}

trait MemberService {
  def getByCommunityId(communityId: String): IO[AppError, List[Member]]
  def getById(id: String): IO[AppError, Option[Member]]
  def create(memberCreateDTO: MemberCreateDTO): IO[AppError, String]
  def delete(id: String): IO[AppError, Unit]
}

object MemberService extends Accessible[MemberService]





