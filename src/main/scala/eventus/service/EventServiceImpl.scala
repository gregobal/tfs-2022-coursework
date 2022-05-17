package eventus.service

import eventus.dto.EventCreateDTO
import eventus.error.AppError
import eventus.model.Event
import eventus.repository.EventRepository
import zio.{IO, URLayer, ZLayer}
import io.scalaland.chimney.dsl.TransformerOps

case class EventServiceImpl(repo: EventRepository) extends EventService {
  override def getByCommunityId(
      communityId: String
  ): IO[AppError, List[Event]] = {
    repo.filterByCommunityId(communityId)
  }

  override def getById(id: String): IO[AppError, Option[Event]] = {
    repo.filterById(id)
  }

  override def create(eventCreateDTO: EventCreateDTO): IO[AppError, String] =
    for {
      id <- zio.Random.nextUUID
      event = eventCreateDTO.into[Event]
        .withFieldConst(_.id, id.toString).transform
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
