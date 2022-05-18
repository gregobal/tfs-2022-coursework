package eventus.repository

import eventus.common.types.{EventId, MemberId}
import eventus.error.RepositoryError
import eventus.model.Participant
import zio.IO

trait ParticipantRepository {
  def filter(
      eventId: EventId,
      memberId: Option[MemberId]
  ): IO[RepositoryError, List[Participant]]
  def insert(participant: Participant): IO[RepositoryError, Unit]
  def delete(participant: Participant): IO[RepositoryError, Unit]
}
