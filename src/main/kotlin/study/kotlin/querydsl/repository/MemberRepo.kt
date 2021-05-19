package study.kotlin.querydsl.repository

import org.springframework.data.jpa.repository.JpaRepository
import study.kotlin.querydsl.entity.Member

interface MemberRepo : JpaRepository<Member, Long>, MemberRepoCustom{
    fun findByUsername(username : String): List<Member>
}