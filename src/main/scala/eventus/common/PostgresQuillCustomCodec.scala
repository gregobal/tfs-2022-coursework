package eventus.common

import io.getquill.MappedEncoding

import java.time.{ZoneId, ZonedDateTime}
import java.util.Date

object PostgresQuillCustomCodec {
  implicit val encodeZonedDateTime: MappedEncoding[ZonedDateTime, Date] =
    MappedEncoding[ZonedDateTime, Date](z => Date.from(z.toInstant))
  implicit val decodeZonedDateTime: MappedEncoding[Date, ZonedDateTime] =
    MappedEncoding[Date, ZonedDateTime](date => ZonedDateTime.ofInstant(date.toInstant, ZoneId.systemDefault()))
}
