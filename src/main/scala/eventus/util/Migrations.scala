package eventus.util

import eventus.config.AppConfig
import org.flywaydb.core.Flyway
import zio.{RIO, ZIO}

object Migrations {
  val migrate: RIO[AppConfig, Unit] = {
    ZIO.serviceWithZIO[AppConfig] { appConfig =>
      val db = appConfig.database
      ZIO.attemptBlockingIO {
        Flyway.configure()
          .dataSource(db.url, db.user, db.password)
          .load()
          .migrate()
      }
    }
  }
}
