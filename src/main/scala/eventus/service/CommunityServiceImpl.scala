package eventus.service

import eventus.dto.CommunityCreateDTO
import eventus.error.AppError
import eventus.model.Community
import eventus.repository.CommunityRepository
import io.scalaland.chimney.dsl.TransformerOps
import zio.{IO, URLayer, ZLayer}

import java.util.UUID

case class CommunityServiceImpl(repo: CommunityRepository)
    extends CommunityService {
  override def getAll: IO[AppError, List[Community]] = {
    repo.queryAll
  }

  override def getById(id: UUID): IO[AppError, Option[Community]] = {
    repo.filterById(id)
  }

  override def create(
      communityCreateDTO: CommunityCreateDTO
  ): IO[AppError, UUID] = for {
    id <- zio.Random.nextUUID
    community = communityCreateDTO
      .into[Community]
      .withFieldConst(_.id, id)
      .transform
    _ <- repo.insert(community)
  } yield community.id

  override def update(community: Community): IO[AppError, Unit] = {
    repo.update(community)
  }
}

object CommunityServiceImpl {
  val live: URLayer[CommunityRepository, CommunityService] =
    ZLayer.fromFunction(CommunityServiceImpl(_))
}
