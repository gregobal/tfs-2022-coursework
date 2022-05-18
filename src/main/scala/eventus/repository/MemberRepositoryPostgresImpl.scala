package eventus.repository

import eventus.common.types.{CommunityId, MemberId}
import eventus.error.RepositoryError
import eventus.model.Member
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio.{IO, URLayer, ZLayer}

import javax.sql.DataSource

case class MemberRepositoryPostgresImpl(dataSource: DataSource)
    extends MemberRepository {

  private val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  private val members = quote(
    querySchema[Member]("member")
  )

  override def filterByCommunityId(
      communityId: CommunityId
  ): IO[RepositoryError, List[Member]] =
    ctx
      .run(
        quote(
          members.filter(_.communityId == lift(communityId))
        )
      )
      .provideService(dataSource)
      .mapError(RepositoryError)

  override def filterById(id: MemberId): IO[RepositoryError, Option[Member]] =
    ctx
      .run(
        quote(
          members.filter(_.id == lift(id))
        )
      )
      .map(_.headOption)
      .provideService(dataSource)
      .mapError(RepositoryError)

  override def insert(event: Member): IO[RepositoryError, Unit] =
    ctx
      .run(
        quote(
          members.insertValue(lift(event))
        )
      )
      .unit
      .provideService(dataSource)
      .mapError(RepositoryError)

  override def delete(id: MemberId): IO[RepositoryError, Unit] =
    ctx
      .run(
        quote(
          members.filter(_.id == lift(id)).delete
        )
      )
      .unit
      .provideService(dataSource)
      .mapError(RepositoryError)
}

object MemberRepositoryPostgresImpl {
  val live: URLayer[DataSource, MemberRepository] =
    ZLayer.fromFunction(MemberRepositoryPostgresImpl(_))
}
