package eventus.service

import eventus.dto.EventCreateDTO
import eventus.error.AppError
import eventus.model.Event
import eventus.repository.EventRepository
import io.scalaland.chimney.dsl.TransformerOps
import zio.{IO, URLayer, ZIO, ZLayer}

import java.util.UUID

case class EventServiceImpl(repo: EventRepository) extends EventService {
  override def getAllOrByCommunityId(
      communityIdOpt: Option[UUID]
  ): IO[AppError, List[Event]] = {
    repo.getAllOrFilterByCommunityId(communityIdOpt)
  }

  override def getById(id: UUID): IO[AppError, Option[Event]] = {
    repo.filterById(id)
  }

  override def create(
      communityId: UUID,
      eventCreateDTO: EventCreateDTO
  ): ZIO[MemberService with NotificationService, AppError, UUID] =
    for {
      id <- zio.Random.nextUUID
      event = eventCreateDTO
        .into[Event]
        .withFieldConst(_.id, id)
        .withFieldConst(_.communityId, communityId)
        .transform
      _ <- repo.insert(event)
      _ <- NotificationService(_.notifyAboutEvent(event))
    } yield event.id

  override def update(event: Event): IO[AppError, Unit] = {
    repo.update(event)
  }
}

object EventServiceImpl {
  val live: URLayer[EventRepository, EventService] =
    ZLayer.fromFunction(EventServiceImpl(_))
}
