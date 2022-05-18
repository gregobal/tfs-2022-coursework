package eventus.model

import eventus.common.types.{EventId, MemberId}

case class Participant(
    memberId: MemberId,
    eventId: EventId
)
