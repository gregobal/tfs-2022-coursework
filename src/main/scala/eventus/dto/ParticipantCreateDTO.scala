package eventus.dto

import java.util.UUID

case class ParticipantCreateDTO(
    memberId: UUID,
    eventId: UUID
)
