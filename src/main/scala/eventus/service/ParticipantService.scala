package eventus.service

import eventus.dto.ParticipantCreateDTO
import eventus.error.AppError
import eventus.model.Participant
import zio.{Accessible, IO}

import java.util.UUID

trait ParticipantService {
  def getById(id: UUID): IO[AppError, Option[Participant]]
  def getByQueryParams(
      eventId: UUID,
      memberId: Option[UUID]
  ): IO[AppError, List[Participant]]
  def create(
      participantCreateDTO: ParticipantCreateDTO
  ): IO[AppError, UUID]
  def delete(id: UUID): IO[AppError, Unit]
}

object ParticipantService extends Accessible[ParticipantService]
