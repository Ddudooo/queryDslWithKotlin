package study.kotlin.querydsl.dto

import com.querydsl.core.annotations.QueryProjection

data class MemberTeamDto
    @QueryProjection
    constructor(
    var memberId : Long,
    var username : String,
    var age :Int,
    var teamId : Long,
    var teamName : String
) {
}