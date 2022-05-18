package eventus.service

import eventus.dto.EventCreateDTO
import eventus.error.AppError
import eventus.model.Event
import zio.{Accessible, IO, ZIO}

import java.util.UUID

trait EventService {
  def getAllOrByCommunityId(
      communityIdOpt: Option[UUID]
  ): IO[AppError, List[Event]]
  def getById(id: UUID): IO[AppError, Option[Event]]
  def create(
      communityId: UUID,
      eventCreateDTO: EventCreateDTO
  ): ZIO[MemberService with NotificationService, AppError, UUID]
  def update(event: Event): IO[AppError, Unit]
}

object EventService extends Accessible[EventService]
