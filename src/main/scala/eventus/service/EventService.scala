package eventus.service

import eventus.dto.EventCreateDTO
import eventus.error.AppError
import eventus.model.Event
import zio.{Accessible, IO}

import java.util.UUID

trait EventService {
  def getByCommunityId(communityId: UUID): IO[AppError, List[Event]]
  def getById(id: UUID): IO[AppError, Option[Event]]
  def create(eventCreateDTO: EventCreateDTO): IO[AppError, UUID]
  def update(event: Event): IO[AppError, Unit]
}

object EventService extends Accessible[EventService]
