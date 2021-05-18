package study.kotlin.querydsl.dto

import com.querydsl.core.annotations.QueryProjection

data class MemberDto
@QueryProjection
constructor(
    var username: String?= null,
    var age: Int? =null
) {
}