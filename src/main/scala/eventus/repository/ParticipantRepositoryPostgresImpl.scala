package eventus.repository

import eventus.error.RepositoryError
import eventus.model.Participant
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio.{IO, URLayer, ZLayer}

import java.util.UUID
import javax.sql.DataSource

case class ParticipantRepositoryPostgresImpl(dataSource: DataSource)
    extends ParticipantRepository {

  private val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  private val participants = quote(
    querySchema[Participant]("participant")
  )

  override def filter(
      eventId: UUID,
      memberId: Option[UUID]
  ): IO[RepositoryError, List[Participant]] = {
    val participantByMember = memberId match {
      case Some(value) =>
        quote(
          participants.filter(_.memberId == lift(value))
        )
      case None => participants
    }

    ctx
      .run(
        quote(
          participantByMember.filter(_.eventId == lift(eventId))
        )
      )
      .provideService(dataSource)
      .mapError(RepositoryError)
  }

  override def insert(participant: Participant): IO[RepositoryError, Unit] =
    ctx
      .run(
        quote(
          participants.insertValue(lift(participant))
        )
      )
      .unit
      .provideService(dataSource)
      .mapError(RepositoryError)

  override def delete(participant: Participant): IO[RepositoryError, Unit] =
    ctx
      .run(
        quote(
          participants
            .filter(p =>
              p.eventId == lift(participant.eventId) && p.memberId == lift(
                participant.memberId
              )
            )
            .delete
        )
      )
      .unit
      .provideService(dataSource)
      .mapError(RepositoryError)
}

object ParticipantRepositoryPostgresImpl {
  val live: URLayer[DataSource, ParticipantRepository] =
    ZLayer.fromFunction(ParticipantRepositoryPostgresImpl(_))
}
