package eventus.service

import eventus.common.types.{CommunityId, EventId}
import eventus.dto.EventCreateDTO
import eventus.error.AppError
import eventus.model.Event
import zio.{Accessible, IO, ZIO}

trait EventService {
  def getAllOrByCommunityId(
      communityIdOpt: Option[CommunityId]
  ): IO[AppError, List[Event]]
  def getById(id: EventId): IO[AppError, Option[Event]]
  def create(
      communityId: CommunityId,
      eventCreateDTO: EventCreateDTO
  ): ZIO[MemberService with NotificationService, AppError, EventId]
  def update(event: Event): IO[AppError, Unit]
}

object EventService extends Accessible[EventService]
