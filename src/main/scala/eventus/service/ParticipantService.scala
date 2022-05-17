package eventus.service

import eventus.dto.ParticipantCreateDTO
import eventus.error.AppError
import eventus.model.Participant
import zio.{Accessible, IO}

trait ParticipantService {
  def getById(id: String): IO[AppError, Option[Participant]]
  def getByQueryParams(eventId: String, memberId: Option[String]): IO[AppError, List[Participant]]
  def create(participantCreateDTO: ParticipantCreateDTO): IO[AppError, String]
  def delete(id: String): IO[AppError, Unit]
}

object ParticipantService extends Accessible[ParticipantService]
