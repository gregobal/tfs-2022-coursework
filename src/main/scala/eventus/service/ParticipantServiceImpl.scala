package eventus.service

import eventus.dto.ParticipantCreateDTO
import eventus.error.AppError
import eventus.model.Participant
import eventus.repository.ParticipantRepository
import zio.{IO, URLayer, ZLayer}

case class ParticipantServiceImpl(repo: ParticipantRepository) extends ParticipantService {

  override def getById(id: String): IO[AppError, Option[Participant]] = {
    repo.filterById(id)
  }

  override def getByQueryParams(eventId: String, memberId: Option[String]): IO[AppError, List[Participant]] = {
    repo.filter(eventId, memberId)
  }

  override def create(participantCreateDTO: ParticipantCreateDTO): IO[AppError, String] = for {
    id <- zio.Random.nextUUID
    ParticipantCreateDTO(memberId, eventId) = participantCreateDTO
    participant = Participant(id.toString, memberId, eventId)
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