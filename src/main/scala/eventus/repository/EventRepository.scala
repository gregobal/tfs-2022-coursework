package eventus.repository

import eventus.error.RepositoryError
import eventus.model.Event
import zio.IO

import java.util.UUID

trait EventRepository {
  def filterByCommunityId(
      communityId: UUID
  ): IO[RepositoryError, List[Event]]
  def filterById(id: UUID): IO[RepositoryError, Option[Event]]
  def insert(event: Event): IO[RepositoryError, Unit]
  def update(event: Event): IO[RepositoryError, Unit]
}
