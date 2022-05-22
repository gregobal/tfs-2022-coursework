package eventus.repository
import eventus.common.types.EventId
import eventus.common.{RepositoryError, types}
import eventus.dto.ReviewCreateDTO
import eventus.model.{Participant, Review}
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio.{IO, URLayer, ZLayer}

import javax.sql.DataSource

case class ReviewRepositoryPostgresImpl(dataSource: DataSource)
    extends ReviewRepository {

  private val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  private val reviews = quote(
    querySchema[Review]("review")
  )
  private val participants = quote(
    querySchema[Participant]("participant")
  )

  override def filterByEventId(
      eventId: types.EventId
  ): IO[RepositoryError, List[Review]] =
    ctx
      .run(
        quote {
          for {
            participant <- participants
              .filter(_.eventId == lift(eventId))
            review <- reviews.filter(_.id == participant.ticket)
          } yield review
        }
      )
      .provideService(dataSource)
      .mapError(RepositoryError)

  override def insert(
      eventId: EventId,
      reviewCreateDTO: ReviewCreateDTO
  ): IO[RepositoryError, Unit] = {
    ctx.transaction(
      for {
        ticket <- ctx.run(
          participants
            .filter(p =>
              p.eventId == lift(eventId) && p.memberId == lift(
                reviewCreateDTO.memberId
              )
            )
            .map(_.ticket)
            .distinct
        )
        _ <- ctx.run(
          reviews.insert(
            _.id -> lift(ticket.last),
            _.rating -> lift(reviewCreateDTO.rating),
            _.feedback -> lift(reviewCreateDTO.feedback)
          )
        )
      } yield ()
    )
  }.provideService(dataSource)
    .mapError(RepositoryError)

}

object ReviewRepositoryPostgresImpl {
  val live: URLayer[DataSource, ReviewRepository] =
    ZLayer.fromFunction(ReviewRepositoryPostgresImpl(_))
}
