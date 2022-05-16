package eventus.repository

import eventus.error.RepositoryError
import eventus.model.Event
import zio._

trait EventRepository {
  def queryAll: IO[RepositoryError, List[Event]]
  def filterById(id: String): IO[RepositoryError, Option[Event]]
  def upsert(event: Event): IO[RepositoryError, Unit]
}
