package eventus.dto

import java.util.UUID

case class MemberCreateDTO(
    email: String,
    communityId: UUID
)
