package eventus.service

import eventus.dto.EventCreateDTO
import eventus.error.AppError
import eventus.model.Event
import eventus.repository.EventRepository
import zio.{IO, ZLayer, URLayer}

case class EventServiceImpl(repo: EventRepository) extends EventService {
  override def getAll: IO[AppError, List[Event]] = {
    repo.queryAll
  }

  override def getById(id: String): IO[AppError, Option[Event]] = {
    repo.filterById(id)
  }

  override def create(eventCreateDTO: EventCreateDTO): IO[AppError, String] = for {
    id <- zio.Random.nextUUID
    EventCreateDTO(title, description, datetime, location, link, capacity) = eventCreateDTO
    event = Event(id.toString, title, description, datetime, location, link, capacity)
    _ <- repo.upsert(event)
  } yield event.id

  override def update(event: Event): IO[AppError, Unit] = {
    repo.upsert(event)
  }
}

object EventServiceImpl {
  val live: URLayer[EventRepository, EventService] = ZLayer.fromFunction(EventServiceImpl(_))
}