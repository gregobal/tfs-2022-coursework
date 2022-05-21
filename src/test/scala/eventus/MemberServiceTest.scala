package eventus

import eventus.common.RepositoryError
import eventus.common.types.{CommunityId, MemberId}
import eventus.dto.MemberCreateDTO
import eventus.model.Member
import eventus.repository.MemberRepository
import eventus.service.{MemberService, MemberServiceImpl}
import io.scalaland.chimney.dsl.TransformerOps
import zio.test.Assertion.isSome
import zio.test.{TestEnvironment, ZIOSpecDefault, ZSpec, assert, assertTrue}
import zio.{IO, Scope, ULayer, ZIO, ZLayer}

import java.util.UUID
import scala.collection.concurrent.TrieMap

object MemberServiceTest extends ZIOSpecDefault {
  private val member = Member(
    MemberId(UUID.fromString("3a917bdd-84d0-4e22-b55c-bc52f063c821")),
    "e@ma.il",
    CommunityId(UUID.fromString("4a917bdd-84d0-4e22-b55c-bc52f063c822")),
    isNotify = false
  )

  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("member service tests")(
      test("getByCommunityId") {
        (for {
          _ <- ZIO.serviceWithZIO[MemberRepository](_.insert(member))
          result <- MemberService(
            _.getByCommunityId(member.communityId)
          )
        } yield assertTrue(result == List(member))).provide(
          MemberServiceImpl.live,
          InMemoryMemberRepository.live
        )
      },
      test("getById") {
        (for {
          _ <- ZIO.serviceWithZIO[MemberRepository](_.insert(member))
          result <- MemberService(_.getById(member.id))
        } yield assert(result)(isSome)).provide(
          MemberServiceImpl.live,
          InMemoryMemberRepository.live
        )
      },
      test("create") {
        (for {
          _ <- MemberService(
            _.create(
              member.communityId,
              member.into[MemberCreateDTO].transform
            )
          )
          result <- ZIO.serviceWithZIO[MemberRepository](
            _.filterByCommunityId(member.communityId)
          )
        } yield assertTrue(result.nonEmpty))
          .provide(
            MemberServiceImpl.live,
            InMemoryMemberRepository.live
          )
      },
      test("delete") {
        (for {
          _ <- ZIO.serviceWithZIO[MemberRepository](_.insert(member))
          _ <- MemberService(_.delete(member.id))
          result <- ZIO.serviceWithZIO[MemberRepository](
            _.filterById(member.id)
          )
        } yield assertTrue(result.isEmpty))
          .provide(
            MemberServiceImpl.live,
            InMemoryMemberRepository.live
          )
      }
    )
}

class InMemoryMemberRepository extends MemberRepository {
  private val map = new TrieMap[MemberId, Member]()

  override def filterByCommunityId(
      communityId: CommunityId
  ): IO[RepositoryError, List[Member]] = IO.succeed(
    map.values.toList
  )

  override def filterById(id: MemberId): IO[RepositoryError, Option[Member]] =
    IO.succeed(
      map.get(id)
    )

  override def insert(member: Member): IO[RepositoryError, Unit] = IO.succeed {
    map.put(member.id, member)
    ()
  }

  override def delete(id: MemberId): IO[RepositoryError, Unit] = IO.succeed {
    map.remove(id)
    ()
  }

  override def updateIsNotify(
      id: MemberId,
      isNotify: Boolean
  ): IO[RepositoryError, Unit] = IO.succeed {
    map.get(id) match {
      case Some(value) => map.replace(id, value.copy(isNotify = isNotify))
      case None        => ()
    }
  }
}

object InMemoryMemberRepository {
  def live: ULayer[MemberRepository] =
    ZLayer.succeed(new InMemoryMemberRepository)
}
