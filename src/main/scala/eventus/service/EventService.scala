package eventus.service

import eventus.dto.EventCreateDTO
import eventus.error.AppError
import eventus.model.Event
import zio.{Accessible, IO}

trait EventService {
  def getByCommunityId(communityId: String): IO[AppError, List[Event]]
  def getById(id: String): IO[AppError, Option[Event]]
  def create(eventCreateDTO: EventCreateDTO): IO[AppError, String]
  def update(event: Event): IO[AppError, Unit]
}

object EventService extends Accessible[EventService]
