package eventus.model

import java.util.UUID

case class Participant(
    memberId: UUID,
    eventId: UUID
)
