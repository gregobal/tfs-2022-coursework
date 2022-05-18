package eventus

import eventus.common.Migration.migrate
import eventus.common.{AppConfig, DataSourceImpl}
import eventus.endpoint.{swagger, zioHttp}
import eventus.repository.{
  CommunityRepositoryPostgresImpl,
  EventRepositoryPostgresImpl,
  MemberRepositoryPostgresImpl,
  ParticipantRepositoryPostgresImpl
}
import eventus.service._
import zhttp.service.Server
import zio.{ExitCode, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault {

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    (for {
      config <- ZIO.service[AppConfig]
      _ <- migrate
      _ <- Server.start(config.http.port, zioHttp ++ swagger)
    } yield ExitCode)
      .provide(
        AppConfig.live,
        EventServiceImpl.live,
        CommunityServiceImpl.live,
        MemberServiceImpl.live,
        ParticipantServiceImpl.live,
        NotificationServiceFakeImpl.live,
        EventRepositoryPostgresImpl.live,
        CommunityRepositoryPostgresImpl.live,
        MemberRepositoryPostgresImpl.live,
        ParticipantRepositoryPostgresImpl.live,
        DataSourceImpl.live
      )
}
