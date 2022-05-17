package eventus.model

import java.util.UUID

case class Member(
    id: UUID,
    email: String,
    communityId: UUID
)
