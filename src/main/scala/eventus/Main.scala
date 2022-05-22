package eventus

import eventus.common.Endpoints.{swagger, zioHttp}
import eventus.common.Migration.migrate
import eventus.common.{AppConfig, DataSourceImpl}
import eventus.community.repository.{
  CommunityRepositoryPostgresImpl,
  MemberRepositoryPostgresImpl
}
import eventus.community.service.{CommunityServiceImpl, MemberServiceImpl}
import eventus.event.repository.{
  EventRepositoryPostgresImpl,
  ParticipantRepositoryPostgresImpl,
  ReviewRepositoryPostgresImpl
}
import eventus.event.service.{
  EventServiceImpl,
  ParticipantServiceImpl,
  ReviewServiceImpl
}
import eventus.notification.service.{
  EmailServiceImpl,
  NotificationQueue,
  NotificationService,
  NotificationServiceQueueImpl
}
import zhttp.service.Server
import zio.{ExitCode, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault {

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    (for {
      config <- ZIO.service[AppConfig]
      _ <- migrate(config.database)
      _ <- Server.start(config.http.port, zioHttp ++ swagger) zipPar
        ZIO.serviceWithZIO[NotificationService](_.processing())
    } yield ExitCode)
      .provide(
        AppConfig.live,
        EventServiceImpl.live,
        CommunityServiceImpl.live,
        MemberServiceImpl.live,
        ParticipantServiceImpl.live,
        ReviewServiceImpl.live,
        NotificationServiceQueueImpl.live,
        NotificationQueue.live,
        EmailServiceImpl.live,
        EventRepositoryPostgresImpl.live,
        CommunityRepositoryPostgresImpl.live,
        MemberRepositoryPostgresImpl.live,
        ParticipantRepositoryPostgresImpl.live,
        ReviewRepositoryPostgresImpl.live,
        DataSourceImpl.live
      )
}
