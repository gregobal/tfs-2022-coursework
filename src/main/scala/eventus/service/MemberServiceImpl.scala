package eventus.service

import eventus.common.AppError
import eventus.common.types.{CommunityId, MemberId}
import eventus.common.validation.DTOValidator.validateMemberCreateDTO
import eventus.dto.MemberCreateDTO
import eventus.model.Member
import eventus.repository.MemberRepository
import io.scalaland.chimney.dsl.TransformerOps
import zio.{IO, URLayer, ZLayer}

case class MemberServiceImpl(repo: MemberRepository) extends MemberService {
  override def getByCommunityId(
      communityId: CommunityId
  ): IO[AppError, List[Member]] = {
    repo.filterByCommunityId(communityId)
  }

  override def getById(id: MemberId): IO[AppError, Option[Member]] = {
    repo.filterById(id)
  }

  override def create(
      communityId: CommunityId,
      memberCreateDTO: MemberCreateDTO
  ): IO[AppError, MemberId] =
    for {
      validated <- validateMemberCreateDTO(memberCreateDTO).toZIO
      id <- zio.Random.nextUUID
      member = validated
        .into[Member]
        .withFieldConst(_.id, MemberId(id))
        .withFieldConst(_.communityId, communityId)
        .transform
      _ <- repo.insert(member)
    } yield member.id

  override def delete(id: MemberId): IO[AppError, Unit] = {
    repo.delete(id)
  }
}

object MemberServiceImpl {
  val live: URLayer[MemberRepository, MemberService] =
    ZLayer.fromFunction(MemberServiceImpl(_))
}
