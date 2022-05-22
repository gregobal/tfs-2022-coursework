package eventus.repository

import eventus.common.RepositoryError
import eventus.common.types.{EventId, MemberId}
import eventus.model.Participant
import zio.IO

trait ParticipantRepository {
  def filter(
      eventId: EventId,
      memberId: Option[MemberId]
  ): IO[RepositoryError, List[Participant]]
  def insert(eventId: EventId, memberId: MemberId): IO[RepositoryError, Unit]
  def delete(eventId: EventId, memberId: MemberId): IO[RepositoryError, Unit]
}
