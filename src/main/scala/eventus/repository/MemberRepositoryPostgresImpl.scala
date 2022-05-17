package eventus.repository

import eventus.error.RepositoryError
import eventus.model.Member
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio.{IO, URLayer, ZLayer}

import javax.sql.DataSource

case class MemberRepositoryPostgresImpl(dataSource: DataSource) extends MemberRepository {

  private val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  private val members = quote(
    querySchema[Member]("member")
  )

  override def filterByCommunityId(communityId: String): IO[RepositoryError, List[Member]] =
    ctx.run(
      quote(
        members.filter(_.communityId == lift(communityId))
      )
    )
      .provideService(dataSource)
      .mapError(ex => RepositoryError(ex))

  override def filterById(id: String): IO[RepositoryError, Option[Member]] =
    ctx.run(
      quote(
        members.filter(_.id == lift(id))
      )
    ).map(_.headOption)
      .provideService(dataSource)
      .mapError(ex => RepositoryError(ex))

  override def insert(event: Member): IO[RepositoryError, Unit] =
    ctx.run(
      quote(
        members.insertValue(lift(event))
      )
    ).unit
      .provideService(dataSource)
      .mapError(ex => RepositoryError(ex))

  override def delete(id: String): IO[RepositoryError, Unit] =
    ctx.run(
      quote(
        members.filter(_.id == lift(id)).delete
      )
    ).unit
      .provideService(dataSource)
      .mapError(ex => RepositoryError(ex))
}

object MemberRepositoryPostgresImpl {
  val live: URLayer[DataSource, MemberRepository] =
    ZLayer.fromFunction(MemberRepositoryPostgresImpl(_))
}

