package eventus.community.dto

import eventus.common.types.MemberId

case class MemberIsNotifyDTO(
    id: MemberId,
    isNotify: Boolean
)
