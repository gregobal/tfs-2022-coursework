package eventus.repository

import eventus.common.types.{CommunityId, EventId}
import eventus.error.RepositoryError
import eventus.model.Event
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio.{IO, URLayer, ZLayer}

import java.time.{ZoneId, ZonedDateTime}
import java.util.Date
import javax.sql.DataSource

case class EventRepositoryPostgresImpl(dataSource: DataSource)
    extends EventRepository {

  private val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  private val events = quote(
    querySchema[Event]("event")
  )

  override def getAllOrFilterByCommunityId(
      communityIdOpt: Option[CommunityId]
  ): IO[RepositoryError, List[Event]] = {
    val filter = communityIdOpt match {
      case Some(value) =>
        quote(
          events.filter(_.communityId == lift(value))
        )
      case None => events
    }
    ctx
      .run(
        quote(
          filter
        )
      )
      .provideService(dataSource)
      .mapError(RepositoryError)
  }

  override def filterById(id: EventId): IO[RepositoryError, Option[Event]] =
    ctx
      .run(
        quote(
          events.filter(_.id == lift(id))
        )
      )
      .map(_.headOption)
      .provideService(dataSource)
      .mapError(RepositoryError)

  override def insert(event: Event): IO[RepositoryError, Unit] =
    ctx
      .run(
        quote(
          events
            .insertValue(lift(event))
        )
      )
      .unit
      .provideService(dataSource)
      .mapError(RepositoryError)

  override def update(event: Event): IO[RepositoryError, Unit] =
    ctx
      .run(
        quote(
          events
            .filter(_.id == lift(event.id))
            .updateValue(lift(event))
        )
      )
      .unit
      .provideService(dataSource)
      .mapError(RepositoryError)

  private implicit val encodeZonedDateTime
      : MappedEncoding[ZonedDateTime, Date] =
    MappedEncoding[ZonedDateTime, Date](z => Date.from(z.toInstant))
  private implicit val decodeZonedDateTime
      : MappedEncoding[Date, ZonedDateTime] =
    MappedEncoding[Date, ZonedDateTime](date =>
      ZonedDateTime.ofInstant(date.toInstant, ZoneId.systemDefault())
    )
}

object EventRepositoryPostgresImpl {
  val live: URLayer[DataSource, EventRepository] =
    ZLayer.fromFunction(EventRepositoryPostgresImpl(_))
}
