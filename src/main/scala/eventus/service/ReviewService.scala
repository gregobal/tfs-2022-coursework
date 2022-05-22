package eventus.service

import eventus.common.AppError
import eventus.common.types.EventId
import eventus.dto.ReviewCreateDTO
import eventus.model.Review
import zio.{Accessible, IO}

trait ReviewService {
  def getByEventId(eventId: EventId): IO[AppError, List[Review]]
  def add(
      eventId: EventId,
      reviewCreateDTO: ReviewCreateDTO
  ): IO[AppError, Unit]
}

object ReviewService extends Accessible[ReviewService]
