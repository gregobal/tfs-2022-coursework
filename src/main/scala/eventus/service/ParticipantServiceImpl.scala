package eventus.service

import eventus.common.AppError
import eventus.model.Participant
import eventus.repository.ParticipantRepository
import eventus.common.types.{EventId, MemberId}
import zio.{IO, URLayer, ZLayer}

import java.util.UUID

case class ParticipantServiceImpl(repo: ParticipantRepository)
    extends ParticipantService {

  def getByEventIdAndFilterByMemberId(
      eventId: EventId,
      memberId: Option[MemberId]
  ): IO[AppError, List[Participant]] = {
    repo.filter(eventId, memberId)
  }

  override def create(
      eventId: EventId,
      memberId: MemberId
  ): IO[AppError, Unit] = {
    repo.insert(Participant(memberId, eventId))
  }

  def delete(eventId: EventId, memberId: MemberId): IO[AppError, Unit] = {
    repo.delete(Participant(memberId, eventId))
  }
}

object ParticipantServiceImpl {
  val live: URLayer[ParticipantRepository, ParticipantService] =
    ZLayer.fromFunction(ParticipantServiceImpl(_))
}
