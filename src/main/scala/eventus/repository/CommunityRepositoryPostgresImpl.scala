package eventus.repository

import eventus.common.RepositoryError
import eventus.common.types.CommunityId
import eventus.model.Community
import io.getquill.{PostgresZioJdbcContext, Query, SnakeCase}
import zio.{IO, URLayer, ZIO, ZLayer}

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
      .mapError(RepositoryError)

  override def filterById(
      id: CommunityId
  ): IO[RepositoryError, Option[Community]] =
    ctx
      .run(
        quote(
          communities.filter(
            _.id == lift(id)
          )
        )
      )
      .map(_.headOption)
      .provideService(dataSource)
      .tapError(err => ZIO.log(err.getMessage))
      .mapError(RepositoryError)

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
      .mapError(RepositoryError)

  override def update(community: Community): IO[RepositoryError, Long] =
    ctx
      .run(
        quote(
          communities
            .filter(_.id == lift(community.id))
            .updateValue(lift(community))
        )
      )
      .provideService(dataSource)
      .mapError(RepositoryError)

  override def likeByWordsArray(
      words: Seq[String]
  ): IO[RepositoryError, List[Community]] = {
    val rawQuery = quote { (w: Seq[String]) =>
      infix"""
          SELECT * FROM community WHERE name ilike any ($w) OR description ilike any ($w)
        """.as[Query[Community]]
    }
    ctx.run(rawQuery(lift(words)))
  }.provideService(dataSource)
    .mapError(RepositoryError)
}

object CommunityRepositoryPostgresImpl {
  val live: URLayer[DataSource, CommunityRepository] =
    ZLayer.fromFunction(CommunityRepositoryPostgresImpl(_))
}
