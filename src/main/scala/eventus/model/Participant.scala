package eventus.model

import eventus.common.types.{EventId, MemberId}

import java.util.UUID

case class Participant(
    memberId: MemberId,
    eventId: EventId,
    ticket: UUID
)
