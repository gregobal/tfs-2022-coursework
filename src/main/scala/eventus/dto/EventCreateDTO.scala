package eventus.dto

import java.time.ZonedDateTime
import java.util.UUID

case class EventCreateDTO(
    communityId: UUID,
    title: String,
    description: Option[String],
    datetime: ZonedDateTime,
    location: Option[String],
    link: Option[String],
    capacity: Option[Int]
)
