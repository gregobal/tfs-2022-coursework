package eventus.service

import eventus.dto.CommunityCreateDTO
import eventus.error.AppError
import eventus.model.Community
import zio.{Accessible, IO}

import java.util.UUID

trait CommunityService {
  def getAll: IO[AppError, List[Community]]
  def getById(id: UUID): IO[AppError, Option[Community]]
  def create(communityCreateDTO: CommunityCreateDTO): IO[AppError, UUID]
  def update(community: Community): IO[AppError, Unit]
  def search(string: String): IO[AppError, List[Community]]
}

object CommunityService extends Accessible[CommunityService]
