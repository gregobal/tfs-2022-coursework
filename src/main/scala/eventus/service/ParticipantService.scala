package eventus.service

import eventus.error.AppError
import eventus.model.Participant
import eventus.common.types.{EventId, MemberId}
import zio.{Accessible, IO}

import java.util.UUID

trait ParticipantService {
  def getByEventIdAndFilterByMemberId(
      eventId: EventId,
      memberId: Option[MemberId]
  ): IO[AppError, List[Participant]]
  def create(eventId: EventId, memberId: MemberId): IO[AppError, Unit]
  def delete(eventId: EventId, memberId: MemberId): IO[AppError, Unit]
}

object ParticipantService extends Accessible[ParticipantService]
