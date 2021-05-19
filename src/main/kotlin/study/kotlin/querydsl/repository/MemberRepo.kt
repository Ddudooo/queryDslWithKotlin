package study.kotlin.querydsl.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import study.kotlin.querydsl.entity.Member

interface MemberRepo : JpaRepository<Member, Long>, MemberRepoCustom, QuerydslPredicateExecutor<Member>{
    fun findByUsername(username : String): List<Member>
}