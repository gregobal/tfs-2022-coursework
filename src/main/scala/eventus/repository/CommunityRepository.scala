package eventus.repository

import eventus.error.RepositoryError
import eventus.model.Community
import zio.IO

import java.util.UUID

trait CommunityRepository {
  def queryAll: IO[RepositoryError, List[Community]]
  def filterById(id: UUID): IO[RepositoryError, Option[Community]]
  def insert(community: Community): IO[RepositoryError, Unit]
  def update(community: Community): IO[RepositoryError, Unit]
}
