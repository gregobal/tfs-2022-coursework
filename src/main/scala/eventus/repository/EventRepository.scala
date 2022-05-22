package eventus.repository

import eventus.common.RepositoryError
import eventus.common.types.{CommunityId, EventId}
import eventus.model.Event
import zio.IO

trait EventRepository {
  def queryAllOrFilterByCommunityId(
      communityIdOpt: Option[CommunityId]
  ): IO[RepositoryError, List[Event]]
  def filterById(id: EventId): IO[RepositoryError, Option[Event]]
  def insert(event: Event): IO[RepositoryError, Unit]
  def update(event: Event): IO[RepositoryError, Long]
}
