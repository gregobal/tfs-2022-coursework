package eventus.model

import eventus.common.types.{CommunityId, MemberId}

case class Member(
    id: MemberId,
    email: String,
    communityId: CommunityId,
    isNotify: Boolean
)
