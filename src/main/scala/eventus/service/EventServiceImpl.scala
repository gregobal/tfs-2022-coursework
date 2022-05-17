package eventus.service

import eventus.dto.EventCreateDTO
import eventus.error.AppError
import eventus.model.Event
import eventus.repository.EventRepository
import io.scalaland.chimney.dsl.TransformerOps
import zio.{IO, URLayer, ZLayer}

import java.util.UUID

case class EventServiceImpl(repo: EventRepository) extends EventService {
  override def getByCommunityId(
      communityId: UUID
  ): IO[AppError, List[Event]] = {
    repo.filterByCommunityId(communityId)
  }

  override def getById(id: UUID): IO[AppError, Option[Event]] = {
    repo.filterById(id)
  }

  override def create(eventCreateDTO: EventCreateDTO): IO[AppError, UUID] =
    for {
      id <- zio.Random.nextUUID
      event = eventCreateDTO
        .into[Event]
        .withFieldConst(_.id, id)
        .transform
      _ <- repo.insert(event)
    } yield event.id

  override def update(event: Event): IO[AppError, Unit] = {
    repo.update(event)
  }
}

object EventServiceImpl {
  val live: URLayer[EventRepository, EventService] =
    ZLayer.fromFunction(EventServiceImpl(_))
}
