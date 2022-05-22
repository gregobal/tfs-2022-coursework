package eventus.service

import eventus.common.validation._
import eventus.common.{AppError, RepositoryError, ServiceError, types}
import eventus.dto.ReviewCreateDTO
import eventus.model.Review
import eventus.repository.ReviewRepository
import org.postgresql.util.PSQLException
import zio.prelude.Validation
import zio.{IO, URLayer, ZLayer}

case class ReviewServiceImpl(repo: ReviewRepository) extends ReviewService {
  override def getByEventId(
      eventId: types.EventId
  ): IO[AppError, List[Review]] = {
    repo.filterByEventId(eventId)
  }

  override def add(
      eventId: types.EventId,
      reviewCreateDTO: ReviewCreateDTO
  ): IO[AppError, Unit] = {
    for {
      validated <- validateToZIO(
        Validation.validateWith(
          Validation.succeed(reviewCreateDTO.memberId),
          for {
            v <- validateIntMin(reviewCreateDTO.rating, "rating", 1)
            _ <- validateIntMax(reviewCreateDTO.rating, "rating", 5)
          } yield v,
          for {
            v <- validateStringFieldNotBlank(
              reviewCreateDTO.feedback,
              "feedback"
            )
            _ <- validateStringMinLength(
              reviewCreateDTO.feedback,
              "feedback",
              10
            )
          } yield v
        )(ReviewCreateDTO)
      )
      _ <- repo
        .insert(eventId, validated)
        .mapError { case RepositoryError(throwable) =>
          throwable match {
            case ex: PSQLException =>
              ex.getSQLState match {
                case s if s == "23505" =>
                  ServiceError(
                    s"Event with id = '$eventId' already reviewed from member with id = '${reviewCreateDTO.memberId}'"
                  )
              }
          }
        }
    } yield ()
  }
}

object ReviewServiceImpl {
  val live: URLayer[ReviewRepository, ReviewService] =
    ZLayer.fromFunction(ReviewServiceImpl(_))
}
