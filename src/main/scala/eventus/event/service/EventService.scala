package eventus.event.service

import eventus.common.AppError
import eventus.common.types.{CommunityId, EventId}
import eventus.event.dto.EventCreateDTO
import eventus.event.model.Event
import eventus.notification.service.NotificationService
import zio.{Accessible, IO, ZIO}

trait EventService {
  def getAllOrByCommunityId(
      communityIdOpt: Option[CommunityId]
  ): IO[AppError, List[Event]]
  def getById(id: EventId): IO[AppError, Option[Event]]
  def create(
      communityId: CommunityId,
      eventCreateDTO: EventCreateDTO
  ): ZIO[NotificationService, AppError, EventId]
  def update(event: Event): IO[AppError, Unit]
}

object EventService extends Accessible[EventService]
