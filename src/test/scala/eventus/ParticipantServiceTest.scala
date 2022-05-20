package eventus

import eventus.common.types.{EventId, MemberId}
import eventus.common.{RepositoryError, types}
import eventus.model.Participant
import eventus.repository.ParticipantRepository
import eventus.service.{ParticipantService, ParticipantServiceImpl}
import zio.test.{TestEnvironment, ZIOSpecDefault, ZSpec, assertTrue}
import zio.{IO, Scope, ULayer, ZIO, ZLayer}

import java.util.UUID
import scala.collection.concurrent.TrieMap

object ParticipantServiceTest extends ZIOSpecDefault {
  private val participant = Participant(
    MemberId(UUID.fromString("3a917bdd-84d0-4e22-b55c-bc52f063c821")),
    EventId(UUID.fromString("3a917bdd-84d0-4e22-b55c-bc52f063c821"))
  )

  override def spec: ZSpec[TestEnvironment with Scope, Any] =
    suite("participant service tests")(
      test("getByEventIdAndFilterByMemberId") {
        (for {
          _ <- ZIO.serviceWithZIO[ParticipantRepository](_.insert(participant))
          result <- ParticipantService(
            _.getByEventIdAndFilterByMemberId(
              participant.eventId,
              Some(participant.memberId)
            )
          )
        } yield assertTrue(result == List(participant))).provide(
          ParticipantServiceImpl.live,
          InMemoryParticipantRepository.live
        )
      },
      test("register") {
        (for {
          _ <- ParticipantService(
            _.register(
              participant.eventId,
              participant.memberId
            )
          )
          result <- ZIO.serviceWithZIO[ParticipantRepository](
            _.filter(
              participant.eventId,
              Some(participant.memberId)
            )
          )
        } yield assertTrue(result.nonEmpty))
          .provide(
            ParticipantServiceImpl.live,
            InMemoryParticipantRepository.live
          )
      },
      test("unregister") {
        (for {
          _ <- ZIO.serviceWithZIO[ParticipantRepository](_.insert(participant))
          _ <- ParticipantService(
            _.unregister(
              participant.eventId,
              participant.memberId
            )
          )
          result <- ZIO.serviceWithZIO[ParticipantRepository](
            _.filter(
              participant.eventId,
              Some(participant.memberId)
            )
          )
        } yield assertTrue(result.isEmpty))
          .provide(
            ParticipantServiceImpl.live,
            InMemoryParticipantRepository.live
          )
      }
    )
}

class InMemoryParticipantRepository extends ParticipantRepository {
  private val map = new TrieMap[(EventId, MemberId), Participant]()

  override def filter(
      eventId: types.EventId,
      memberId: Option[types.MemberId]
  ): IO[RepositoryError, List[Participant]] = IO.succeed(
    map.values.toList.filter(p =>
      p.eventId == eventId && (memberId.isEmpty || p.memberId == memberId.get)
    )
  )

  override def insert(participant: Participant): IO[RepositoryError, Unit] =
    IO.succeed {
      map.put((participant.eventId, participant.memberId), participant)
      ()
    }

  override def delete(participant: Participant): IO[RepositoryError, Unit] =
    IO.succeed {
      map.remove((participant.eventId, participant.memberId))
      ()
    }
}

object InMemoryParticipantRepository {
  def live: ULayer[ParticipantRepository] =
    ZLayer.succeed(new InMemoryParticipantRepository)
}
