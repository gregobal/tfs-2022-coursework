package eventus

import eventus.common.RepositoryError
import eventus.common.types.{EventId, MemberId}
import eventus.event.dto.ReviewCreateDTO
import eventus.event.model.Review
import eventus.event.repository.ReviewRepository
import eventus.event.service.{ReviewService, ReviewServiceImpl}
import zio.test.{TestEnvironment, ZIOSpecDefault, ZSpec, assertTrue}
import zio.{IO, Scope, ULayer, ZIO, ZLayer}

import java.util.UUID
import scala.collection.concurrent.TrieMap

object ReviewServiceTest extends ZIOSpecDefault {
  private val reviewDTO = ReviewCreateDTO(
    MemberId(UUID.randomUUID()),
    5,
    "Some feedback"
  )
  private val eventId = EventId(UUID.randomUUID())

  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("review service tests")(
      test("getByEventId") {
        (for {
          _ <- ZIO.serviceWithZIO[ReviewRepository](
            _.insert(eventId, reviewDTO)
          )
          result <- ReviewService(
            _.getByEventId(eventId)
          )
        } yield assertTrue(result.size == 1)).provide(
          ReviewServiceImpl.live,
          InMemoryReviewRepository.live
        )
      },
      test("add") {
        (for {
          _ <- ReviewService(_.add(eventId, reviewDTO))
          result <- ZIO.serviceWithZIO[ReviewRepository](
            _.filterByEventId(eventId)
          )
        } yield assertTrue(
          result.head.feedback == reviewDTO.feedback &&
            result.head.rating == reviewDTO.rating
        )).provide(
          ReviewServiceImpl.live,
          InMemoryReviewRepository.live
        )
      }
    )
}

class InMemoryReviewRepository extends ReviewRepository {
  private val map = new TrieMap[MemberId, List[(EventId, Review)]]()

  override def filterByEventId(
      eventId: EventId
  ): IO[RepositoryError, List[Review]] = IO.succeed(
    map.values.toList.flatMap(_.filter(_._1 == eventId).map(_._2))
  )

  override def insert(
      eventId: EventId,
      reviewCreateDTO: ReviewCreateDTO
  ): IO[RepositoryError, Unit] = IO.succeed {
    val review = Review(
      UUID.randomUUID(),
      reviewCreateDTO.rating,
      reviewCreateDTO.feedback
    )
    map.update(
      reviewCreateDTO.memberId,
      map.get(reviewCreateDTO.memberId) match {
        case Some(value) => (eventId, review) :: value
        case None        => List((eventId, review))
      }
    )
  }
}

object InMemoryReviewRepository {
  def live: ULayer[ReviewRepository] =
    ZLayer.succeed(new InMemoryReviewRepository)
}
