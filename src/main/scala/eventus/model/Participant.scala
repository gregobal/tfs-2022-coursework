package eventus.model

import java.util.UUID

case class Participant(
    id: UUID,
    memberId: UUID,
    eventId: UUID
)
