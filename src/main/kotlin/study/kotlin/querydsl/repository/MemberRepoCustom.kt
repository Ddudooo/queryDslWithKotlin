package study.kotlin.querydsl.repository

import study.kotlin.querydsl.dto.MemberSearchCondition
import study.kotlin.querydsl.dto.MemberTeamDto

interface MemberRepoCustom {

    fun search(condition : MemberSearchCondition):List<MemberTeamDto>
}