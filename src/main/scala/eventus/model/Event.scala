package eventus.model

import java.time.ZonedDateTime
import java.util.UUID

case class Event(
    id: UUID,
    communityId: UUID,
    title: String,
    description: Option[String],
    datetime: ZonedDateTime,
    location: Option[String],
    link: Option[String],
    capacity: Option[Int]
)
