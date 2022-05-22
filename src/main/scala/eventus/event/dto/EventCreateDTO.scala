package eventus.event.dto

import java.time.ZonedDateTime

case class EventCreateDTO(
    title: String,
    description: Option[String],
    datetime: ZonedDateTime,
    location: Option[String],
    link: Option[String],
    capacity: Option[Int]
)
