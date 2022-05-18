package eventus.common

import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype
import io.getquill.MappedEncoding
import sttp.tapir.Schema
import sttp.tapir.SchemaType.SString

import java.util.UUID
import scala.util.Try

package object types {

  @newtype case class CommunityId(value: UUID)
  object CommunityId {
    implicit val schema: Schema[CommunityId] = Schema(SString())
    implicit val circeEncoder: Encoder[CommunityId] =
      Encoder.encodeString.contramap(_.toString)
    implicit val circeDecoder: Decoder[CommunityId] =
      Decoder.decodeString.emapTry(str =>
        Try(CommunityId(UUID.fromString(str)))
      )
    implicit val quillEncoder: MappedEncoding[CommunityId, UUID] =
      MappedEncoding(_.value)
    implicit val quillDecoder: MappedEncoding[UUID, CommunityId] =
      MappedEncoding(CommunityId(_))
  }

  @newtype case class EventId(value: UUID)
  object EventId {
    implicit val schema: Schema[EventId] = Schema(SString())
    implicit val circeEncoder: Encoder[EventId] =
      Encoder.encodeString.contramap(_.toString)
    implicit val circeDecoder: Decoder[EventId] =
      Decoder.decodeString.emapTry(str => Try(EventId(UUID.fromString(str))))
    implicit val quillEncoder: MappedEncoding[EventId, UUID] =
      MappedEncoding(_.value)
    implicit val quillDecoder: MappedEncoding[UUID, EventId] =
      MappedEncoding(EventId(_))
  }

  @newtype case class MemberId(value: UUID)
  object MemberId {
    implicit val schema: Schema[MemberId] = Schema(SString())
    implicit val circeEncoder: Encoder[MemberId] =
      Encoder.encodeString.contramap(_.toString)
    implicit val circeDecoder: Decoder[MemberId] =
      Decoder.decodeString.emapTry(str => Try(MemberId(UUID.fromString(str))))
    implicit val quillEncoder: MappedEncoding[MemberId, UUID] =
      MappedEncoding(_.value)
    implicit val quillDecoder: MappedEncoding[UUID, MemberId] =
      MappedEncoding(MemberId(_))
  }
}
