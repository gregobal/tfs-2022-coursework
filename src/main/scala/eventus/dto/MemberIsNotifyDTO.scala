package eventus.dto

import eventus.common.types.MemberId

case class MemberIsNotifyDTO(
    id: MemberId,
    isNotify: Boolean
)
