package eventus.repository

import eventus.error.RepositoryError
import eventus.model.Event
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio.{IO, ZLayer}

import javax.sql.DataSource

case class PostgresEventRepository(dataSource: DataSource) extends EventRepository {
  import eventus.util.PostgresQuillCustomCodec.{encodeZonedDateTime, decodeZonedDateTime}

  private val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  override def queryAll: IO[RepositoryError, List[Event]] =
    ctx.run (
      quote(
        querySchema[Event]("event")
      )
    )
      .provideService(dataSource)
      .mapError(ex => RepositoryError(ex))

  override def filterById(id: String): IO[RepositoryError, Option[Event]] =
    ctx.run(
      quote(
        querySchema[Event]("event").filter(_.id == lift(id))
      )
    ).map(_.headOption)
      .provideService(dataSource)
      .mapError(ex => RepositoryError(ex))

  override def upsert(event: Event): IO[RepositoryError, Unit] =
    ctx.run(
      quote(
        querySchema[Event]("event")
          .insertValue(lift(event))
          .onConflictUpdate(_.id)(
            (t, e) => t.title -> e.title,
            (t, e) => t.description -> e.description,
            (t, e) => t.datetime -> e.datetime,
            (t, e) => t.location -> e.location,
            (t, e) => t.link -> e.link,
            (t, e) => t.capacity -> e.capacity
          )
      )
    ).unit
      .provideService(dataSource)
      .mapError(ex => RepositoryError(ex))
}

object PostgresEventRepository {
  val live: ZLayer[DataSource, Nothing, EventRepository] =
    ZLayer.fromFunction(PostgresEventRepository(_))
}
