package eventus.service

import eventus.common.{AppError, ServiceError, ValidationError}
import eventus.common.types.{CommunityId, EventId}
import eventus.common.validation.{
  validateStringFieldNotBlank,
  validateStringMinLength,
  validateToZIO,
  validateZoneDateTimeIsFuture
}
import eventus.dto.EventCreateDTO
import eventus.model.Event
import eventus.repository.EventRepository
import io.scalaland.chimney.dsl.TransformerOps
import zio.prelude.Validation
import zio.{IO, URLayer, ZIO, ZLayer}

case class EventServiceImpl(repo: EventRepository) extends EventService {
  override def getAllOrByCommunityId(
      communityIdOpt: Option[CommunityId]
  ): IO[AppError, List[Event]] = {
    repo.queryAllOrFilterByCommunityId(communityIdOpt)
  }

  override def getById(id: EventId): IO[AppError, Option[Event]] = {
    repo.filterById(id)
  }

  override def create(
      communityId: CommunityId,
      eventCreateDTO: EventCreateDTO
  ): ZIO[NotificationService, AppError, EventId] =
    for {
      id <- zio.Random.nextUUID
      event = eventCreateDTO
        .into[Event]
        .withFieldConst(_.id, EventId(id))
        .withFieldConst(_.communityId, communityId)
        .transform
      validated <- validateEvent(event)
      _ <- repo.insert(validated)
      _ <- NotificationService(_.addNotifyAboutEvent(validated))
    } yield event.id

  override def update(event: Event): IO[AppError, Unit] = {
    (for {
      validated <- validateEvent(event)
      r <- repo.update(validated)
    } yield r)
      .filterOrFail(_ == 1)(
        ServiceError(
          s"Error while updating, possibly not found event with id = ${event.id}"
        )
      )
      .unit
  }

  private def validateEvent(event: Event): IO[ValidationError, Event] =
    validateToZIO(
      Validation.validateWith(
        Validation.succeed(event.id),
        Validation.succeed(event.communityId),
        for {
          v <- validateStringFieldNotBlank(event.title, "title")
          _ <- validateStringMinLength(event.title, "title", 6)
        } yield v,
        Validation.succeed(event.description),
        validateZoneDateTimeIsFuture(event.datetime, "datetime"),
        Validation.succeed(event.location),
        Validation.succeed(event.link),
        Validation.succeed(event.capacity)
      )(Event)
    )
}

object EventServiceImpl {
  val live: URLayer[EventRepository, EventService] =
    ZLayer.fromFunction(EventServiceImpl(_))
}
