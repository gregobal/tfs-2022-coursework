package eventus.service

import eventus.dto.CommunityCreateDTO
import eventus.error.AppError
import eventus.model.Community
import eventus.repository.CommunityRepository
import zio.{IO, URLayer, ZLayer}

case class CommunityServiceImpl(repo: CommunityRepository) extends CommunityService {
  override def getAll: IO[AppError, List[Community]] = {
    repo.queryAll
  }

  override def getById(id: String): IO[AppError, Option[Community]] = {
    repo.filterById(id)
  }

  override def create(communityCreateDTO: CommunityCreateDTO): IO[AppError, String] = for {
    id <- zio.Random.nextUUID
    CommunityCreateDTO(name, description) = communityCreateDTO
    community = Community(id.toString, name, description)
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
