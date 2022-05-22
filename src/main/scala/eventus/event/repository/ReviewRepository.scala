package eventus.event.repository

import eventus.common.RepositoryError
import eventus.common.types.EventId
import eventus.event.dto.ReviewCreateDTO
import eventus.event.model.Review
import zio.IO

trait ReviewRepository {
  def filterByEventId(eventId: EventId): IO[RepositoryError, List[Review]]
  def insert(
      eventId: EventId,
      reviewCreateDTO: ReviewCreateDTO
  ): IO[RepositoryError, Unit]
}
