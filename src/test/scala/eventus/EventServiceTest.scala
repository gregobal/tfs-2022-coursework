package eventus

import eventus.common.types.{CommunityId, EventId, MemberId}
import eventus.common.{AppError, RepositoryError}
import eventus.community.dto.{MemberCreateDTO, MemberIsNotifyDTO}
import eventus.community.model.Member
import eventus.community.service.MemberService
import eventus.event.dto.EventCreateDTO
import eventus.event.model.Event
import eventus.event.repository.EventRepository
import eventus.event.service.{EventService, EventServiceImpl}
import eventus.notification.service.NotificationService
import io.scalaland.chimney.dsl.TransformerOps
import zio.test.Assertion.isSome
import zio.test.{TestEnvironment, ZIOSpecDefault, ZSpec, assert, assertTrue}
import zio.{IO, Scope, UIO, ULayer, ZIO, ZLayer}

import java.time.ZonedDateTime
import java.util.UUID
import scala.collection.concurrent.TrieMap

object EventServiceTests extends ZIOSpecDefault {
  private val event = Event(
    EventId(UUID.fromString("3a917bdd-84d0-4e22-b55c-bc52f063c821")),
    CommunityId(UUID.fromString("4a917bdd-84d0-4e22-b55c-bc52f063c822")),
    "test title",
    Some("description"),
    ZonedDateTime.now().plusDays(1),
    Some("Zimbabwe"),
    Some("http://eventus/test"),
    Some(100)
  )

  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("event service tests")(
      test("getAllOrByCommunityId") {
        (for {
          _ <- ZIO.serviceWithZIO[EventRepository](_.insert(event))
          result <- EventService(
            _.getAllOrByCommunityId(Some(event.communityId))
          )
        } yield assertTrue(result == List(event))).provide(
          InMemoryEventRepository.live,
          EventServiceImpl.live
        )
      },
      test("getById") {
        (for {
          _ <- ZIO.serviceWithZIO[EventRepository](_.insert(event))
          result <- EventService(_.getById(event.id))
        } yield assert(result)(isSome)).provide(
          InMemoryEventRepository.live,
          EventServiceImpl.live
        )
      },
      test("create") {
        (for {
          _ <- EventService(
            _.create(
              event.communityId,
              event.into[EventCreateDTO].transform
            )
          )
          result <- ZIO.serviceWithZIO[EventRepository](
            _.queryAllOrFilterByCommunityId(None)
          )
        } yield assertTrue(result.nonEmpty))
          .provide(
            InMemoryEventRepository.live,
            EventServiceImpl.live,
            NotificationServiceFake.live
          )
      },
      test("update") {
        val updatedEvent = event.copy(title = "updated event")
        (for {
          _ <- ZIO.serviceWithZIO[EventRepository](_.insert(event))
          _ <- EventService(_.update(updatedEvent))
          list <- ZIO.serviceWithZIO[EventRepository](
            _.queryAllOrFilterByCommunityId(None)
          )
          actual = list.head
        } yield assertTrue(actual == updatedEvent))
          .provide(
            InMemoryEventRepository.live,
            EventServiceImpl.live
          )
      }
    )
}

class InMemoryEventRepository extends EventRepository {
  private val map = new TrieMap[EventId, Event]()

  override def queryAllOrFilterByCommunityId(
      communityIdOpt: Option[CommunityId]
  ): IO[RepositoryError, List[Event]] = IO.succeed(
    map.values.toList
  )

  def filterById(id: EventId): IO[RepositoryError, Option[Event]] = IO.succeed(
    map.get(id)
  )

  def insert(event: Event): IO[RepositoryError, Unit] = IO.succeed {
    map.put(event.id, event)
    ()
  }

  override def update(event: Event): IO[RepositoryError, Long] = IO.succeed {
    map.replace(event.id, event)
    1L
  }
}

object InMemoryEventRepository {
  def live: ULayer[EventRepository] =
    ZLayer.succeed(new InMemoryEventRepository)
}

object NotificationServiceFake extends NotificationService {
  override def addNotifyAboutEvent(
      event: Event
  ): UIO[Unit] = ZIO.succeed(())

  override def processing(): UIO[Unit] = ZIO.succeed(())

  def live: ULayer[NotificationService] =
    ZLayer.succeed(NotificationServiceFake)
}

object MemberServiceFake {
  def live: ULayer[MemberService] = ZLayer.succeed(new MemberService {
    override def getByCommunityId(
        communityId: CommunityId
    ): IO[AppError, List[Member]] = IO.succeed(List.empty)
    override def getById(id: MemberId): IO[AppError, Option[Member]] =
      ZIO.none
    override def create(
        communityId: CommunityId,
        memberCreateDTO: MemberCreateDTO
    ): IO[AppError, MemberId] =
      IO.succeed(MemberId(UUID.randomUUID()))
    override def delete(id: MemberId): IO[AppError, Unit] = ZIO.unit
    override def setNotify(
        memberIsNotifyDTO: MemberIsNotifyDTO
    ): IO[AppError, Unit] = ZIO.unit
  })
}
