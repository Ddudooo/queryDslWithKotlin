package study.kotlin.querydsl.repository

import com.querydsl.core.types.dsl.BooleanExpression
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import study.kotlin.querydsl.dto.MemberSearchCondition
import study.kotlin.querydsl.entity.Member
import study.kotlin.querydsl.entity.QMember
import study.kotlin.querydsl.entity.QMember.member
import study.kotlin.querydsl.entity.QTeam
import study.kotlin.querydsl.entity.QTeam.team
import study.kotlin.querydsl.repository.support.Querydsl4RepositorySupport

@Repository
class MemberTestRepo: Querydsl4RepositorySupport(Member::class.java) {

    fun basicSelect(): MutableList<Member>?
    = select(member)
      .from(member)
      .fetch()

    fun basicSelectFrom(): MutableList<Member>?
    = selectFrom(member).fetch()

    fun searchPageByApplyPage(condition: MemberSearchCondition, pageable: Pageable): Page<Member> {
        val query = selectFrom(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe)
            )

        val content = querydsl!!.applyPagination(pageable, query).fetch()
        return PageableExecutionUtils.getPage(content, pageable, query::fetchCount)
    }

    fun applyPagingation(condition: MemberSearchCondition, pageable: Pageable): Page<Member> {
        return applyPagination<Member>(pageable) { query ->
            query.selectFrom(member)
                .leftJoin(member.team, team)
                .where(
                    usernameEq(condition.username),
                    teamNameEq(condition.teamName),
                    ageGoe(condition.ageGoe),
                    ageLoe(condition.ageLoe)
                )
        }
    }

    fun applyPagingation2(condition: MemberSearchCondition, pageable: Pageable): Page<Member> {
        return applyPagination<Member>(pageable,
        {
            it.selectFrom(member)
                .leftJoin(member.team, team)
                .where(
                    usernameEq(condition.username),
                    teamNameEq(condition.teamName),
                    ageGoe(condition.ageGoe),
                    ageLoe(condition.ageLoe)
                )
        }, {
                it.select(member.id)
                .from(member)
                .leftJoin(member.team, team)
                .where(
                    usernameEq(condition.username),
                    teamNameEq(condition.teamName),
                    ageGoe(condition.ageGoe),
                    ageLoe(condition.ageLoe)
                )
        })
    }

    private fun ageLoe(ageLoeCond: Int?): BooleanExpression? {
        return if(ageLoeCond != null) {
            QMember.member.age.loe(ageLoeCond)
        } else{
            null
        }
    }

    private fun ageGoe(ageGoeCond: Int?): BooleanExpression? {
        return if(ageGoeCond != null) {
            QMember.member.age.goe(ageGoeCond)
        } else{
            null
        }
    }

    private fun teamNameEq(teamNameCond: String?): BooleanExpression? {
        return if(teamNameCond!=null){
            QTeam.team.name.eq(teamNameCond)
        }else{
            null
        }
    }

    private fun usernameEq(nameCond: String?): BooleanExpression? {
        return if(nameCond!=null){
            QMember.member.username.eq(nameCond)
        }else{
            null
        }
    }
}