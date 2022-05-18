package eventus.service

import eventus.common.types.CommunityId
import eventus.dto.CommunityCreateDTO
import eventus.error.AppError
import eventus.model.Community
import zio.{Accessible, IO}

trait CommunityService {
  def getAll: IO[AppError, List[Community]]
  def getById(id: CommunityId): IO[AppError, Option[Community]]
  def create(communityCreateDTO: CommunityCreateDTO): IO[AppError, CommunityId]
  def update(community: Community): IO[AppError, Unit]
  def search(string: String): IO[AppError, List[Community]]
}

object CommunityService extends Accessible[CommunityService]
