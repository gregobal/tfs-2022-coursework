package eventus.repository

import eventus.error.RepositoryError
import eventus.model.Community
import zio.IO

trait CommunityRepository {
  def queryAll: IO[RepositoryError, List[Community]]
  def filterById(id: String): IO[RepositoryError, Option[Community]]
  def insert(community: Community): IO[RepositoryError, Unit]
  def update(community: Community): IO[RepositoryError, Unit]
}
