package eventus.service

import eventus.error.RepositoryError
import eventus.model.Event
import eventus.repository.EventRepository
import zio.test.ZIOSpecDefault
import zio.{IO, Layer, TaskLayer, ULayer, ZIO, ZLayer}
import zio.test.Assertion._
import zio.test._

import scala.collection.concurrent.TrieMap
import java.time.ZonedDateTime

object EventServiceTests extends ZIOSpecDefault {
  override def spec: Spec[Any, TestFailure[Any], TestSuccess] = suite("event service tests")(
    test("getById") {
      val expected = Event(
        "3a917bdd-84d0-4e22-b55c-bc52f063c821",
        "test",
        Some("description"),
        ZonedDateTime.now(),
        Some("Zimbabwe"),
        Some("http://eventus/test"),
        Some(100)
      )

      (for {
        _      <- ZIO.serviceWithZIO[EventRepository](_.upsert(expected))
        result <- EventService(_.getById(expected.id))
      } yield assert(result)(isSome)).provideLayer(testLayer)
    }
  )

  val testLayer: ZLayer[Any, Any, EventRepository with EventService] =
    InMemoryEventRepository.live >+> EventServiceImpl.live
}


class InMemoryEventRepository extends EventRepository {

  private val map = new TrieMap[String, Event]()

  def queryAll: IO[RepositoryError, List[Event]] = IO.succeed(
    map.values.toList
  )

  def filterById(id: String): IO[RepositoryError, Option[Event]] = IO.succeed(
    map.get(id)
  )

  def upsert(event: Event): IO[RepositoryError, Unit] = {
    IO.succeed {
      map.put(event.id, event)
      ()
    }
  }
}

object InMemoryEventRepository {
  def live: ULayer[EventRepository] = ZLayer.succeed(new InMemoryEventRepository)
}