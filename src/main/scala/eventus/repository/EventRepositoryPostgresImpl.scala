package eventus.repository

import eventus.error.RepositoryError
import eventus.model.Event
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio.{IO, URLayer, ZLayer}

import javax.sql.DataSource

case class EventRepositoryPostgresImpl(dataSource: DataSource)
    extends EventRepository {

  import eventus.common.PostgresQuillCustomCodec.{encodeZonedDateTime, decodeZonedDateTime}


  private val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  private val events = quote(
    querySchema[Event]("event")
  )

  override def filterByCommunityId(
      communityId: String
  ): IO[RepositoryError, List[Event]] =
    ctx
      .run(
        quote(
          events.filter(_.communityId == lift(communityId))
        )
      )
      .provideService(dataSource)
      .mapError(ex => RepositoryError(ex))

  override def filterById(id: String): IO[RepositoryError, Option[Event]] =
    ctx
      .run(
        quote(
          events.filter(_.id == lift(id))
        )
      )
      .map(_.headOption)
      .provideService(dataSource)
      .mapError(ex => RepositoryError(ex))

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
      .mapError(ex => RepositoryError(ex))

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
      .mapError(ex => RepositoryError(ex))
}

object EventRepositoryPostgresImpl {
  val live: URLayer[DataSource, EventRepository] =
    ZLayer.fromFunction(EventRepositoryPostgresImpl(_))
}
