package eventus.service

import eventus.error.AppError
import eventus.model.Participant
import eventus.repository.ParticipantRepository
import zio.{IO, URLayer, ZLayer}

import java.util.UUID

case class ParticipantServiceImpl(repo: ParticipantRepository)
    extends ParticipantService {

  def getByEventIdAndFilterByMemberId(
      eventId: UUID,
      memberId: Option[UUID]
  ): IO[AppError, List[Participant]] = {
    repo.filter(eventId, memberId)
  }

  override def create(eventId: UUID, memberId: UUID): IO[AppError, Unit] = {
    repo.insert(Participant(eventId, memberId))
  }

  def delete(eventId: UUID, memberId: UUID): IO[AppError, Unit] = {
    repo.delete(Participant(eventId, memberId))
  }
}

object ParticipantServiceImpl {
  val live: URLayer[ParticipantRepository, ParticipantService] =
    ZLayer.fromFunction(ParticipantServiceImpl(_))
}
