package eventus.repository

import eventus.error.RepositoryError
import eventus.model.Event
import zio.IO

trait EventRepository {
  def filterByCommunityId(communityId: String): IO[RepositoryError, List[Event]]
  def filterById(id: String): IO[RepositoryError, Option[Event]]
  def insert(event: Event): IO[RepositoryError, Unit]
  def update(event: Event): IO[RepositoryError, Unit]
}
