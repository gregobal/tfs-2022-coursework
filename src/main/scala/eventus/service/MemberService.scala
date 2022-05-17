package eventus.service

import eventus.dto.MemberCreateDTO
import eventus.error.AppError
import eventus.model.Member
import zio.{Accessible, IO}

import java.util.UUID

trait MemberService {
  def getByCommunityId(communityId: UUID): IO[AppError, List[Member]]
  def getById(id: UUID): IO[AppError, Option[Member]]
  def create(memberCreateDTO: MemberCreateDTO): IO[AppError, UUID]
  def delete(id: UUID): IO[AppError, Unit]
}

object MemberService extends Accessible[MemberService]
