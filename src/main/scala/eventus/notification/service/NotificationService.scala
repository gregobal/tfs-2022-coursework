package eventus.notification.service

import eventus.community.service.MemberService
import eventus.event.model.Event
import zio.{Accessible, Queue, Schedule, UIO, URIO, URLayer, ZIO, ZLayer}

trait NotificationService {
  def processing(): URIO[MemberService with EmailService, Unit]
  def addNotifyAboutEvent(
      event: Event
  ): UIO[Unit]
}

object NotificationService extends Accessible[NotificationService]

object NotificationQueue {
  val live: ZLayer[Any, Nothing, Queue[Event]] = ZLayer.fromZIO(
    Queue.unbounded[Event]
  )
}

case class NotificationServiceQueueImpl(
    queue: Queue[Event]
) extends NotificationService {

  def processing(): URIO[MemberService with EmailService, Unit] = {
    (for {
      f <- queue.take.fork
      event <- f.join
      members <- MemberService(_.getByCommunityId(event.communityId))
      emailList = members.filter(_.isNotify).map(_.email)
      _ <- ZIO
        .foreachPar(emailList) { email =>
          EmailService(
            _.send(
              email,
              event.title,
              s"${event.title}\n${event.datetime}\n${event.location}\n${event.description}"
            )
          )
        }
        .tapError(ex => ZIO.logError(ex.getMessage))
        .unit
    } yield ())
      .orElse(ZIO.unit)
      .repeat(
        Schedule.forever
      )
      .unit
  }

  def addNotifyAboutEvent(
      event: Event
  ): UIO[Unit] = {
    for {
      _ <- queue.offer(event)
    } yield ()
  }
}

object NotificationServiceQueueImpl {
  val live: URLayer[Queue[Event], NotificationService] =
    ZLayer.fromFunction(NotificationServiceQueueImpl(_))
}
