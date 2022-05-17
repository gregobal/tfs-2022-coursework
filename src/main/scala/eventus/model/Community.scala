package eventus.model

import java.util.UUID

case class Community(
    id: UUID,
    name: String,
    description: Option[String]
)
