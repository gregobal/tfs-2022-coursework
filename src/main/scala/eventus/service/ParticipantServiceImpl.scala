package eventus.service

import eventus.common.{AppError, RepositoryError, ServiceError}
import eventus.model.Participant
import eventus.repository.ParticipantRepository
import eventus.common.types.{EventId, MemberId}
import org.flywaydb.core.internal.database.postgresql.PostgreSQLType
import org.postgresql.util.PSQLException
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

  override def register(
      eventId: EventId,
      memberId: MemberId
  ): IO[AppError, Unit] = {
    repo
      .insert(Participant(memberId, eventId))
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
    repo.delete(Participant(memberId, eventId))
  }
}

object ParticipantServiceImpl {
  val live: URLayer[ParticipantRepository, ParticipantService] =
    ZLayer.fromFunction(ParticipantServiceImpl(_))
}
