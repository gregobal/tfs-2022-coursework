package eventus.common

import org.flywaydb.core.Flyway
import zio.{Task, ZIO}

object Migration {
  def migrate(dbConfig: DbConfig): Task[Unit] = {
    ZIO.attemptBlockingIO {
      Flyway
        .configure()
        .dataSource(dbConfig.url, dbConfig.user, dbConfig.password)
        .load()
        .migrate()
    }
  }
}
