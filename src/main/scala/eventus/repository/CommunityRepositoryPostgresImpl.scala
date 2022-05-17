package eventus.repository

import eventus.error.RepositoryError
import eventus.model.Community
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio.{IO, URLayer, ZLayer}

import javax.sql.DataSource

case class CommunityRepositoryPostgresImpl(dataSource: DataSource)
    extends CommunityRepository {

  private val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  private val communities = quote(
    querySchema[Community]("community")
  )

  override def queryAll: IO[RepositoryError, List[Community]] =
    ctx
      .run(
        communities
      )
      .provideService(dataSource)
      .mapError(ex => RepositoryError(ex))

  override def filterById(id: String): IO[RepositoryError, Option[Community]] =
    ctx
      .run(
        quote(
          communities.filter(_.id == lift(id))
        )
      )
      .map(_.headOption)
      .provideService(dataSource)
      .mapError(ex => RepositoryError(ex))

  override def insert(community: Community): IO[RepositoryError, Unit] =
    ctx
      .run(
        quote(
          communities
            .insertValue(lift(community))
        )
      )
      .unit
      .provideService(dataSource)
      .mapError(ex => RepositoryError(ex))

  override def update(community: Community): IO[RepositoryError, Unit] =
    ctx
      .run(
        quote(
          communities
            .filter(_.id == lift(community.id))
            .updateValue(lift(community))
        )
      )
      .unit
      .provideService(dataSource)
      .mapError(ex => RepositoryError(ex))
}

object CommunityRepositoryPostgresImpl {
  val live: URLayer[DataSource, CommunityRepositoryPostgresImpl] =
    ZLayer.fromFunction(CommunityRepositoryPostgresImpl(_))
}
