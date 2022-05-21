package eventus

import eventus.common.types.{CommunityId, EventId}
import eventus.model.Event
import eventus.service.{
  EmailService,
  NotificationQueue,
  NotificationService,
  NotificationServiceQueueImpl
}
import zio.test.{TestEnvironment, ZIOSpecDefault, ZSpec, assertTrue}
import zio.{Scope, Task, ULayer, ZLayer}

import java.time.ZonedDateTime
import java.util.UUID

// in progress
object NotificationServiceTest extends ZIOSpecDefault {
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
    suite("notification service tests")(
      test("notify") {
        (for {
          _ <- NotificationService(_.addNotifyAboutEvent(event))
        } yield assertTrue(true))
          .provide(
            NotificationServiceQueueImpl.live,
            NotificationQueue.live
          )
      }
    )
}

class EmailServiceFake extends EmailService {
  override def send(to: String, subject: String, content: String): Task[Unit] =
    Task.succeed(to + subject + content)
}

object EmailServiceFake {
  def live: ULayer[EmailService] =
    ZLayer.succeed(new EmailServiceFake)
}
