package eventus.repository

import eventus.common.RepositoryError
import eventus.common.types.EventId
import eventus.dto.ReviewCreateDTO
import eventus.model.Review
import zio.IO

trait ReviewRepository {
  def filterByEventId(eventId: EventId): IO[RepositoryError, List[Review]]
  def insert(
      eventId: EventId,
      reviewCreateDTO: ReviewCreateDTO
  ): IO[RepositoryError, Unit]
}
