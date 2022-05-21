package eventus.common

import pureconfig.ConfigSource
import pureconfig.generic.auto._
import zio.{ULayer, ZIO, ZLayer}

final case class DbConfig(
    url: String,
    user: String,
    password: String
)

final case class HttpConfig(
    host: String,
    port: Int
)

final case class EmailConfig(
    host: String,
    port: Int,
    user: String,
    password: String
)

final case class AppConfig(
    database: DbConfig,
    http: HttpConfig,
    email: EmailConfig
)

object AppConfig {
  val live: ULayer[AppConfig] = ZLayer.fromZIO(
    ZIO
      .attempt(ConfigSource.default.loadOrThrow[AppConfig])
      .orDieWith(err =>
        new RuntimeException("Error loading configuration. " + err.getMessage)
      )
  )
}
