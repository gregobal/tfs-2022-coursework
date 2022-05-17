package eventus.common

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import zio.{URLayer, ZIO, ZLayer}

import javax.sql.DataSource

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
