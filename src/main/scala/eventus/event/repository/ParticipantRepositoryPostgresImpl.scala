package eventus.event.repository

import eventus.common.RepositoryError
import eventus.common.types.{EventId, MemberId}
import eventus.event.model.Participant
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio.{IO, URLayer, ZLayer}

import javax.sql.DataSource

case class ParticipantRepositoryPostgresImpl(dataSource: DataSource)
    extends ParticipantRepository {

  private val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  private val participants = quote(
    querySchema[Participant]("participant")
  )

  override def filter(
      eventId: EventId,
      memberId: Option[MemberId]
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

  override def insert(
      eventId: EventId,
      memberId: MemberId
  ): IO[RepositoryError, Unit] =
    ctx
      .run(
        quote(
          participants
            .insert(_.eventId -> lift(eventId), _.memberId -> lift(memberId))
        )
      )
      .unit
      .provideService(dataSource)
      .mapError(RepositoryError)

  override def delete(
      eventId: EventId,
      memberId: MemberId
  ): IO[RepositoryError, Unit] =
    ctx
      .run(
        quote(
          participants
            .filter(p =>
              p.eventId == lift(eventId) && p.memberId == lift(memberId)
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
