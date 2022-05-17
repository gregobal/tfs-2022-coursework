package eventus.service

import eventus.dto.MemberCreateDTO
import eventus.error.AppError
import eventus.model.Member
import eventus.repository.MemberRepository
import io.scalaland.chimney.dsl.TransformerOps
import zio.{IO, URLayer, ZLayer}

import java.util.UUID

case class MemberServiceImpl(repo: MemberRepository) extends MemberService {
  override def getByCommunityId(
      communityId: UUID
  ): IO[AppError, List[Member]] = {
    repo.filterByCommunityId(communityId)
  }

  override def getById(id: UUID): IO[AppError, Option[Member]] = {
    repo.filterById(id)
  }

  override def create(
      memberCreateDTO: MemberCreateDTO
  ): IO[AppError, UUID] =
    for {
      id <- zio.Random.nextUUID
      member = memberCreateDTO
        .into[Member]
        .withFieldConst(_.id, id)
        .transform
      _ <- repo.insert(member)
    } yield member.id

  override def delete(id: UUID): IO[AppError, Unit] = {
    repo.delete(id)
  }
}

object MemberServiceImpl {
  val live: URLayer[MemberRepository, MemberService] =
    ZLayer.fromFunction(MemberServiceImpl(_))
}
