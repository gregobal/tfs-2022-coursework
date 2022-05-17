package eventus.repository

import eventus.error.RepositoryError
import eventus.model.Participant
import zio.IO

trait ParticipantRepository {
  def filterById(id: String): IO[RepositoryError, Option[Participant]]
  def filter(
      eventId: String,
      memberId: Option[String]
  ): IO[RepositoryError, List[Participant]]
  def insert(participant: Participant): IO[RepositoryError, Unit]
  def delete(id: String): IO[RepositoryError, Unit]
}
