package eventus.service

import eventus.error.RepositoryError
import eventus.model.Event
import eventus.repository.EventRepository
import zio.test.Assertion._
import zio.test.{ZIOSpecDefault, _}
import zio.{IO, ULayer, ZIO, ZLayer}

import java.time.ZonedDateTime
import java.util.UUID
import scala.collection.concurrent.TrieMap

object EventServiceTests extends ZIOSpecDefault {
  override def spec: Spec[Any, TestFailure[Any], TestSuccess] =
    suite("event service tests")(
      test("getById") {
        val expected = Event(
          UUID.fromString("3a917bdd-84d0-4e22-b55c-bc52f063c821"),
          UUID.fromString("4a917bdd-84d0-4e22-b55c-bc52f063c822"),
          "test",
          Some("description"),
          ZonedDateTime.now(),
          Some("Zimbabwe"),
          Some("http://eventus/test"),
          Some(100)
        )

        (for {
          _ <- ZIO.serviceWithZIO[EventRepository](_.insert(expected))
          result <- EventService(_.getById(expected.id))
        } yield assert(result)(isSome)).provideLayer(testLayer)
      }
    )

  val testLayer: ZLayer[Any, Any, EventRepository with EventService] =
    InMemoryEventRepository.live >+> EventServiceImpl.live
}

class InMemoryEventRepository extends EventRepository {

  private val map = new TrieMap[UUID, Event]()

  override def getAllOrFilterByCommunityId(
      communityIdOpt: Option[UUID]
  ): IO[RepositoryError, List[Event]] = IO.succeed(
    map.values.toList
  )

  def filterById(id: UUID): IO[RepositoryError, Option[Event]] = IO.succeed(
    map.get(id)
  )

  def insert(event: Event): IO[RepositoryError, Unit] = IO.succeed {
    map.put(event.id, event)
    ()
  }

  override def update(event: Event): IO[RepositoryError, Unit] = IO.succeed {
    map.replace(event.id, event)
    ()
  }
}

object InMemoryEventRepository {
  def live: ULayer[EventRepository] =
    ZLayer.succeed(new InMemoryEventRepository)
}
