package eventus.event.model

import eventus.common.types.{CommunityId, EventId}

import java.time.ZonedDateTime

case class Event(
    id: EventId,
    communityId: CommunityId,
    title: String,
    description: Option[String],
    datetime: ZonedDateTime,
    location: Option[String],
    // TODO - change String to URL
    link: Option[String],
    capacity: Option[Int]
)
