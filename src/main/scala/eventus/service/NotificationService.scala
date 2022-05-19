package eventus.service

import eventus.common.AppError
import eventus.model.Event
import zio.{Accessible, ULayer, ZIO, ZLayer}

trait NotificationService {
  def notifyAboutEvent(event: Event): ZIO[MemberService, AppError, Unit]
}

object NotificationService extends Accessible[NotificationService]

case class NotificationServiceFakeImpl() extends NotificationService {

  def notifyAboutEvent(event: Event): ZIO[MemberService, AppError, Unit] = {
    for {
      members <- MemberService(_.getByCommunityId(event.communityId))
      emailList = members.map(_.email)
      _ <- ZIO.foreachPar(emailList) { email =>
        ZIO.log(email + ": " + event)
      }
    } yield ()
  }
}

object NotificationServiceFakeImpl {
  val live: ULayer[NotificationServiceFakeImpl] =
    ZLayer.succeed(NotificationServiceFakeImpl())
}
