package eventus.repository

import eventus.error.RepositoryError
import eventus.model.Member
import zio.IO

trait MemberRepository {
  def filterByCommunityId(
      communityId: String
  ): IO[RepositoryError, List[Member]]
  def filterById(id: String): IO[RepositoryError, Option[Member]]
  def insert(member: Member): IO[RepositoryError, Unit]
  def delete(id: String): IO[RepositoryError, Unit]
}
