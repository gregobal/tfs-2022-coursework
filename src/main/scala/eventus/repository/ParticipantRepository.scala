package eventus.repository

import eventus.error.RepositoryError
import eventus.model.Participant
import zio.IO

import java.util.UUID

trait ParticipantRepository {
  def filter(
      eventId: UUID,
      memberId: Option[UUID]
  ): IO[RepositoryError, List[Participant]]
  def insert(participant: Participant): IO[RepositoryError, Unit]
  def delete(participant: Participant): IO[RepositoryError, Unit]
}
