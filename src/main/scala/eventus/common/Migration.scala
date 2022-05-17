package eventus.common

import org.flywaydb.core.Flyway
import zio.{RIO, ZIO}

object Migration {
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
