package eventus.service

import eventus.error.AppError
import eventus.model.Participant
import zio.{Accessible, IO}

import java.util.UUID

trait ParticipantService {
  def getByEventIdAndFilterByMemberId(
      eventId: UUID,
      memberId: Option[UUID]
  ): IO[AppError, List[Participant]]
  def create(eventId: UUID, memberId: UUID): IO[AppError, Unit]
  def delete(eventId: UUID, memberId: UUID): IO[AppError, Unit]
}

object ParticipantService extends Accessible[ParticipantService]
