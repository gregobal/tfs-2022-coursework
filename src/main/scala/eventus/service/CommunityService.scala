package eventus.service

import eventus.dto.CommunityCreateDTO
import eventus.error.AppError
import eventus.model.Community
import zio.{Accessible, IO}

trait CommunityService {
  def getAll: IO[AppError, List[Community]]
  def getById(id: String): IO[AppError, Option[Community]]
  def create(communityCreateDTO: CommunityCreateDTO): IO[AppError, String]
  def update(community: Community): IO[AppError, Unit]
}

object CommunityService extends Accessible[CommunityService]