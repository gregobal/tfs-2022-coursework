package eventus

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import eventus.config.{AppConfig, DbConfig}
import eventus.endpoint.EventEndpoint
import eventus.repository.{EventRepository, PostgresEventRepository}
import eventus.service.{EventService, EventServiceImpl}
import eventus.util.Migrations.migrate
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import zhttp.service.Server
import zio._

import javax.sql.DataSource

object Main extends ZIOAppDefault {

  private type EffectType[A] = RIO[EventService, A]
  private val swaggerEndpoints = SwaggerInterpreter()
    .fromEndpoints[Task](EventEndpoint.routes.map(_.endpoint), "Eventus", "0.1.0")
    .asInstanceOf[List[ServerEndpoint[ZioStreams, EffectType]]]

  private val endpoints = ZioHttpInterpreter[EventService]().toHttp(
    EventEndpoint.routes ++
      swaggerEndpoints
  )

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    (for {
      config <- ZIO.service[AppConfig]
      _ <- migrate
      _ <- Server.start(config.http.port, endpoints)
    } yield ExitCode)
      .provide(
        AppConfig.live,
        EventServiceImpl.live,
        PostgresEventRepository.live,
        DataSourceImpl.live
      )
}

object DataSourceImpl {
  val live: URLayer[AppConfig, DataSource] = ZLayer(
    for {
      config <- ZIO.service[AppConfig]
      dataSource = getDataSource(config.database)
    } yield dataSource
  )

  private def getDataSource(dbConfig: DbConfig): DataSource = {
    val hc = new HikariConfig()
    hc.setJdbcUrl(dbConfig.url)
    hc.setUsername(dbConfig.user)
    hc.setPassword(dbConfig.password)
    new HikariDataSource(hc)
  }
}
