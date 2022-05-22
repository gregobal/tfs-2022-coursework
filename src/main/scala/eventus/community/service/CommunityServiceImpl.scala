package eventus.community.service

import eventus.common.types.CommunityId
import eventus.common.validation.{
  validateStringFieldNotBlank,
  validateStringMinLength,
  validateToZIO
}
import eventus.common.{AppError, ServiceError, ValidationError}
import eventus.community.dto.CommunityCreateDTO
import eventus.community.model.Community
import eventus.community.repository.CommunityRepository
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
      id <- zio.Random.nextUUID
      community = communityCreateDTO
        .into[Community]
        .withFieldConst(_.id, CommunityId(id))
        .transform
      validated <- validateCommunity(community)
      _ <- repo.insert(validated)
    } yield community.id
  }

  override def update(community: Community): IO[AppError, Unit] = {
    (for {
      validated <- validateCommunity(community)
      r <- repo.update(validated)
    } yield r)
      .filterOrFail(_ == 1)(
        ServiceError(
          s"Error while updating, possibly not found community with id = ${community.id}"
        )
      )
      .unit
  }

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

  private def validateCommunity(
      community: Community
  ): IO[ValidationError, Community] =
    validateToZIO(
      Validation.validateWith(
        Validation.succeed(community.id),
        for {
          v <- validateStringFieldNotBlank(community.name, "name")
          _ <- validateStringMinLength(community.name, "name", 2)
        } yield v,
        Validation.succeed(community.description)
      )(Community)
    )
}

object CommunityServiceImpl {
  val live: URLayer[CommunityRepository, CommunityService] =
    ZLayer.fromFunction(CommunityServiceImpl(_))
}
