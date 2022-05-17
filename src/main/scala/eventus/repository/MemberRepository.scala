package eventus.repository

import eventus.error.RepositoryError
import eventus.model.Member
import zio.IO

import java.util.UUID

trait MemberRepository {
  def filterByCommunityId(
      communityId: UUID
  ): IO[RepositoryError, List[Member]]
  def filterById(id: UUID): IO[RepositoryError, Option[Member]]
  def insert(member: Member): IO[RepositoryError, Unit]
  def delete(id: UUID): IO[RepositoryError, Unit]
}
