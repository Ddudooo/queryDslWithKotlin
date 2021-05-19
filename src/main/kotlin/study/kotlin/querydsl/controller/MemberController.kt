package study.kotlin.querydsl.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import study.kotlin.querydsl.dto.MemberSearchCondition
import study.kotlin.querydsl.dto.MemberTeamDto
import study.kotlin.querydsl.repository.MemberJpaRepo
import study.kotlin.querydsl.repository.MemberRepo

@RestController
class MemberController(
    private val memberJpaRepo: MemberJpaRepo,
    private val memberRepo : MemberRepo
) {
    @GetMapping("/v1/members")
    fun searchMemberV1(
        condition: MemberSearchCondition
    ) : List<MemberTeamDto> = memberJpaRepo.search(condition)

    @GetMapping("/v2/members")
    fun searchMemberV2(
        condition: MemberSearchCondition,
        pageable: Pageable
    ) : Page<MemberTeamDto> = memberRepo.searchPageSimple(condition, pageable)

    @GetMapping("/v3/members")
    fun searchMemberV3(
        condition: MemberSearchCondition,
        pageable: Pageable
    ) : Page<MemberTeamDto> = memberRepo.searchPageComplex(condition, pageable)

}