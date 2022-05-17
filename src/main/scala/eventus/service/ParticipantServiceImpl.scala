package eventus.service

import eventus.dto.ParticipantCreateDTO
import eventus.error.AppError
import eventus.model.Participant
import eventus.repository.ParticipantRepository
import io.scalaland.chimney.dsl.TransformerOps
import zio.{IO, URLayer, ZLayer}

import java.util.UUID

case class ParticipantServiceImpl(repo: ParticipantRepository)
    extends ParticipantService {

  override def getById(id: UUID): IO[AppError, Option[Participant]] = {
    repo.filterById(id)
  }

  override def getByQueryParams(
      eventId: UUID,
      memberId: Option[UUID]
  ): IO[AppError, List[Participant]] = {
    repo.filter(eventId, memberId)
  }

  override def create(
      participantCreateDTO: ParticipantCreateDTO
  ): IO[AppError, UUID] = for {
    id <- zio.Random.nextUUID
    participant = participantCreateDTO
      .into[Participant]
      .withFieldConst(_.id, id)
      .transform
    _ <- repo.insert(participant)
  } yield participant.id

  override def delete(id: UUID): IO[AppError, Unit] = {
    repo.delete(id)
  }
}

object ParticipantServiceImpl {
  val live: URLayer[ParticipantRepository, ParticipantService] =
    ZLayer.fromFunction(ParticipantServiceImpl(_))
}
