package eventus.repository

import eventus.error.RepositoryError
import eventus.model.Participant
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

  override def filterById(
      id: String
  ): IO[RepositoryError, Option[Participant]] =
    ctx
      .run(
        quote(
          participants.filter(_.id == lift(id))
        )
      )
      .map(_.headOption)
      .provideService(dataSource)
      .mapError(ex => RepositoryError(ex))

  override def filter(
      eventId: String,
      memberId: Option[String]
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
      .mapError(ex => RepositoryError(ex))
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
      .mapError(ex => RepositoryError(ex))

  override def delete(id: String): IO[RepositoryError, Unit] =
    ctx
      .run(
        quote(
          participants.filter(_.id == lift(id)).delete
        )
      )
      .unit
      .provideService(dataSource)
      .mapError(ex => RepositoryError(ex))
}

object ParticipantRepositoryPostgresImpl {
  val live: URLayer[DataSource, ParticipantRepository] =
    ZLayer.fromFunction(ParticipantRepositoryPostgresImpl(_))
}
