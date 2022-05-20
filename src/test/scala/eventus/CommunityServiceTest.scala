package eventus

import eventus.common.types.CommunityId
import eventus.common.{RepositoryError, types}
import eventus.dto.CommunityCreateDTO
import eventus.model.Community
import eventus.repository.CommunityRepository
import eventus.service.{CommunityService, CommunityServiceImpl}
import io.scalaland.chimney.dsl.TransformerOps
import zio.test.Assertion.isSome
import zio.{IO, Scope, ULayer, ZIO, ZLayer}
import zio.test.{TestEnvironment, ZIOSpecDefault, ZSpec, assert, assertTrue}

import java.util.UUID
import scala.collection.concurrent.TrieMap

object CommunityServiceTest extends ZIOSpecDefault {
  private val community = Community(
    CommunityId(UUID.fromString("4a917bdd-84d0-4e22-b55c-bc52f063c822")),
    "test name",
    Some("description")
  )

  override def spec: ZSpec[TestEnvironment with Scope, Any] = {
    suite("community service tests")(
      test("getAll") {
        (for {
          _ <- ZIO.serviceWithZIO[CommunityRepository](_.insert(community))
          result <- CommunityService(
            _.getAll
          )
        } yield assertTrue(result == List(community)))
          .provide(CommunityServiceImpl.live, InMemoryCommunityRepository.live)
      },
      test("getById") {
        (for {
          _ <- ZIO.serviceWithZIO[CommunityRepository](_.insert(community))
          result <- CommunityService(_.getById(community.id))
        } yield assert(result)(isSome)).provide(
          InMemoryCommunityRepository.live,
          CommunityServiceImpl.live
        )
      },
      test("create") {
        (for {
          _ <- CommunityService(
            _.create(community.into[CommunityCreateDTO].transform)
          )
          result <- ZIO.serviceWithZIO[CommunityRepository](
            _.queryAll
          )
        } yield assertTrue(result.nonEmpty))
          .provide(
            InMemoryCommunityRepository.live,
            CommunityServiceImpl.live
          )
      },
      test("update") {
        val updatedCommunity = community.copy(name = "updated community")
        (for {
          _ <- ZIO.serviceWithZIO[CommunityRepository](_.insert(community))
          _ <- CommunityService(_.update(updatedCommunity))
          list <- ZIO.serviceWithZIO[CommunityRepository](
            _.queryAll
          )
          actual = list.head
        } yield assertTrue(actual == updatedCommunity))
          .provide(
            InMemoryCommunityRepository.live,
            CommunityServiceImpl.live
          )
      }
    )
  }
}

class InMemoryCommunityRepository extends CommunityRepository {
  private val map = new TrieMap[CommunityId, Community]()

  override def queryAll: IO[RepositoryError, List[Community]] = IO.succeed(
    map.values.toList
  )

  override def filterById(
      id: types.CommunityId
  ): IO[RepositoryError, Option[Community]] = IO.succeed(
    map.get(id)
  )

  override def insert(community: Community): IO[RepositoryError, Unit] =
    IO.succeed {
      map.put(community.id, community)
      ()
    }

  override def update(community: Community): IO[RepositoryError, Unit] =
    IO.succeed {
      map.replace(community.id, community)
      ()
    }

  override def likeByWordsArray(
      words: Seq[String]
  ): IO[RepositoryError, List[Community]] = queryAll
}

object InMemoryCommunityRepository {
  def live: ULayer[CommunityRepository] = ZLayer.succeed(
    new InMemoryCommunityRepository
  )
}
