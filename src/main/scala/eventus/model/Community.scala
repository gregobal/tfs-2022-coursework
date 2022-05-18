package eventus.model

import eventus.common.types.CommunityId

case class Community(
    id: CommunityId,
    name: String,
    description: Option[String]
)
