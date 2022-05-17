package eventus.service

import eventus.dto.MemberCreateDTO
import eventus.error.AppError
import eventus.model.Member
import eventus.repository.MemberRepository
import zio.{IO, URLayer, ZLayer}

case class MemberServiceImpl(repo: MemberRepository) extends MemberService {
  override def getByCommunityId(communityId: String): IO[AppError, List[Member]] = {
    repo.filterByCommunityId(communityId)
  }

  override def getById(id: String): IO[AppError, Option[Member]] = {
    repo.filterById(id)
  }

  override def create(memberCreateDTO: MemberCreateDTO): IO[AppError, String] = for {
    id <- zio.Random.nextUUID
    MemberCreateDTO(email, communityId) = memberCreateDTO
    member = Member(id.toString, email, communityId)
    _ <- repo.insert(member)
  } yield member.id

  override def delete(id: String): IO[AppError, Unit] = {
    repo.delete(id)
  }
}

object MemberServiceImpl {
  val live: URLayer[MemberRepository, MemberService] = ZLayer.fromFunction(MemberServiceImpl(_))
}
