package eventus.service

import eventus.common.types.CommunityId
import eventus.common.validation.{
  validateToZIO,
  validateStringFieldNotBlank,
  validateStringMinLength
}
import eventus.common.{AppError, ServiceError, validation}
import eventus.dto.CommunityCreateDTO
import eventus.model.Community
import eventus.repository.CommunityRepository
import io.scalaland.chimney.dsl.TransformerOps
import zio.prelude.Validation
import zio.{IO, Task, URLayer, ZLayer}

import java.net.URLDecoder

case class CommunityServiceImpl(repo: CommunityRepository)
    extends CommunityService {
  override def getAll: IO[AppError, List[Community]] = {
    repo.queryAll
  }

  override def getById(id: CommunityId): IO[AppError, Option[Community]] = {
    repo.filterById(id)
  }

  override def create(
      communityCreateDTO: CommunityCreateDTO
  ): IO[AppError, CommunityId] = {
    for {
      validated <- validateToZIO(
        Validation.validateWith(
          for {
            v <- validateStringFieldNotBlank(communityCreateDTO.name, "name")
            _ <- validateStringMinLength(communityCreateDTO.name, "name", 2)
          } yield v,
          Validation.succeed(communityCreateDTO.description)
        )(CommunityCreateDTO)
      )
      id <- zio.Random.nextUUID
      community = validated
        .into[Community]
        .withFieldConst(_.id, CommunityId(id))
        .transform
      _ <- repo.insert(community)
    } yield community.id
  }

  override def update(community: Community): IO[AppError, Unit] = {
    repo.update(community)
  }

  // TODO - временно так себе реализация поиска, заменить на эффективный сервис поиска
  override def search(string: String): IO[AppError, List[Community]] = {
    for {
      words <- Task
        .succeed {
          URLDecoder
            .decode(string, "UTF-8")
            .split("\\s")
            .toList
            .filter(_.length > 1)
            .map(w => s"%${w.toLowerCase}%")
        }
        .filterOrElseWith(_.nonEmpty)(_ =>
          IO.fail(ServiceError("Search string or its words is too short"))
        )
      result <- repo.likeByWordsArray(words)
    } yield result
  }
}

object CommunityServiceImpl {
  val live: URLayer[CommunityRepository, CommunityService] =
    ZLayer.fromFunction(CommunityServiceImpl(_))
}
