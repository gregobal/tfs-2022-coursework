package eventus.model

import java.util.UUID

case class Review(
    id: UUID,
    rating: Int,
    feedback: String
)
