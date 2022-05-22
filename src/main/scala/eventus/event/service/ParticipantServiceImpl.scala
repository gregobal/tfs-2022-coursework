package eventus.event.service

import eventus.common.types.{EventId, MemberId}
import eventus.common.{AppError, RepositoryError, ServiceError}
import eventus.event.model.Participant
import eventus.event.repository.ParticipantRepository
import org.postgresql.util.PSQLException
import zio.{IO, URLayer, ZLayer}

case class ParticipantServiceImpl(repo: ParticipantRepository)
    extends ParticipantService {

  def getByEventIdAndFilterByMemberId(
      eventId: EventId,
      memberId: Option[MemberId]
  ): IO[AppError, List[Participant]] = {
    repo.filter(eventId, memberId)
  }

  override def register(
      eventId: EventId,
      memberId: MemberId
  ): IO[AppError, Unit] = {
    repo
      .insert(eventId, memberId)
      .mapError { case RepositoryError(throwable) =>
        throwable match {
          case ex: PSQLException =>
            ex.getSQLState match {
              case s if s == "23505" =>
                ServiceError(
                  s"Member with id = '$memberId' already register on event id = '$eventId'"
                )
            }
        }
      }
  }

  def unregister(eventId: EventId, memberId: MemberId): IO[AppError, Unit] = {
    repo.delete(eventId, memberId)
  }
}

object ParticipantServiceImpl {
  val live: URLayer[ParticipantRepository, ParticipantService] =
    ZLayer.fromFunction(ParticipantServiceImpl(_))
}
