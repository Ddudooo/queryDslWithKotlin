package study.kotlin.querydsl.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import study.kotlin.querydsl.dto.MemberSearchCondition
import study.kotlin.querydsl.dto.MemberTeamDto
import study.kotlin.querydsl.repository.MemberJpaRepo

@RestController
class MemberController(
    private val memberJpaRepo: MemberJpaRepo
) {
    @GetMapping("/v1/members")
    fun searchMemberV1(condition: MemberSearchCondition) : List<MemberTeamDto> {
        return memberJpaRepo.search(condition)
    }
}