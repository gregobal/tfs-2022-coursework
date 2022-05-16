package eventus.model

import java.time.ZonedDateTime

case class Event(
                  id: String,
                  title: String,
                  description: Option[String],
                  datetime: ZonedDateTime,
                  location: Option[String],
                  link: Option[String],
                  capacity: Option[Int]
                )
