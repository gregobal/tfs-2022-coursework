package eventus.dto

import java.time.ZonedDateTime

case class EventCreateDTO(
                           communityId: String,
                           title: String,
                           description: Option[String],
                           datetime: ZonedDateTime,
                           location: Option[String],
                           link: Option[String],
                           capacity: Option[Int]
                         )
