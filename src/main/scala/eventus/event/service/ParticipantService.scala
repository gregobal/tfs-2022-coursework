package eventus.event.service

import eventus.common.AppError
import eventus.common.types.{EventId, MemberId}
import eventus.event.model.Participant
import zio.{Accessible, IO}

trait ParticipantService {
  def getByEventIdAndFilterByMemberId(
      eventId: EventId,
      memberId: Option[MemberId]
  ): IO[AppError, List[Participant]]
  def register(eventId: EventId, memberId: MemberId): IO[AppError, Unit]
  def unregister(eventId: EventId, memberId: MemberId): IO[AppError, Unit]
}

object ParticipantService extends Accessible[ParticipantService]
