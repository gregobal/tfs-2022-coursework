package eventus.service

import eventus.dto.ParticipantCreateDTO
import eventus.error.AppError
import eventus.model.Participant
import eventus.repository.ParticipantRepository
import zio.{IO, URLayer, ZLayer}
import io.scalaland.chimney.dsl.TransformerOps

case class ParticipantServiceImpl(repo: ParticipantRepository)
    extends ParticipantService {

  override def getById(id: String): IO[AppError, Option[Participant]] = {
    repo.filterById(id)
  }

  override def getByQueryParams(
      eventId: String,
      memberId: Option[String]
  ): IO[AppError, List[Participant]] = {
    repo.filter(eventId, memberId)
  }

  override def create(
      participantCreateDTO: ParticipantCreateDTO
  ): IO[AppError, String] = for {
    id <- zio.Random.nextUUID
    participant = participantCreateDTO.into[Participant]
      .withFieldConst(_.id, id.toString).transform
    _ <- repo.insert(participant)
  } yield participant.id

  override def delete(id: String): IO[AppError, Unit] = {
    repo.delete(id)
  }
}

object ParticipantServiceImpl {
  val live: URLayer[ParticipantRepository, ParticipantService] =
    ZLayer.fromFunction(ParticipantServiceImpl(_))
}
