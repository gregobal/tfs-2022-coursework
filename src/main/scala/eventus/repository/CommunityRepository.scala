package eventus.repository

import eventus.common.RepositoryError
import eventus.common.types.CommunityId
import eventus.model.Community
import zio.IO

trait CommunityRepository {
  def queryAll: IO[RepositoryError, List[Community]]
  def filterById(id: CommunityId): IO[RepositoryError, Option[Community]]
  def insert(community: Community): IO[RepositoryError, Unit]
  def update(community: Community): IO[RepositoryError, Long]
  def likeByWordsArray(
      words: Seq[String]
  ): IO[RepositoryError, List[Community]]
}
