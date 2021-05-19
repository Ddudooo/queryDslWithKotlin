package study.kotlin.querydsl.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import study.kotlin.querydsl.dto.MemberSearchCondition
import study.kotlin.querydsl.dto.MemberTeamDto

interface MemberRepoCustom {

    fun search(condition : MemberSearchCondition):List<MemberTeamDto>
    fun searchPageSimple(condition: MemberSearchCondition, pageable: Pageable) : Page<MemberTeamDto>
    fun searchPageComplex(condition: MemberSearchCondition, pageable: Pageable) : Page<MemberTeamDto>
}