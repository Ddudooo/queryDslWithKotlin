package study.kotlin.querydsl.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import study.kotlin.querydsl.dto.MemberSearchCondition
import study.kotlin.querydsl.dto.MemberTeamDto
import study.kotlin.querydsl.dto.QMemberTeamDto
import study.kotlin.querydsl.entity.QMember
import study.kotlin.querydsl.entity.QMember.member
import study.kotlin.querydsl.entity.QTeam
import study.kotlin.querydsl.entity.QTeam.team

class MemberRepoCustomImpl(
    private val queryFactory: JPAQueryFactory
) : MemberRepoCustom {
    override fun search(condition: MemberSearchCondition): List<MemberTeamDto> {
        return queryFactory
            .select(
                QMemberTeamDto(
                    member.id.`as`("memberId"),
                    member.username,
                    member.age,
                    team.id.`as`("teamId"),
                    team.name
                )
            )
            .from(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe)
            )
            .fetch()
    }

    override fun searchPageSimple(
        condition: MemberSearchCondition,
        pageable: Pageable,
    ): Page<MemberTeamDto> {
        val results = queryFactory
            .select(
                QMemberTeamDto(
                    member.id.`as`("memberId"),
                    member.username,
                    member.age,
                    team.id.`as`("teamId"),
                    team.name
                )
            )
            .from(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetchResults()

        val content = results.results
        val total = results.total

        return PageImpl(content, pageable, total)
    }

    override fun searchPageComplex(
        condition: MemberSearchCondition,
        pageable: Pageable,
    ): Page<MemberTeamDto> {
        val content = queryFactory
            .select(
                QMemberTeamDto(
                    member.id.`as`("memberId"),
                    member.username,
                    member.age,
                    team.id.`as`("teamId"),
                    team.name
                )
            )
            .from(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

//        val total = queryFactory
//            .selectFrom(member)
//            .leftJoin(member.team, team)
//            .where(
//                usernameEq(condition.username),
//                teamNameEq(condition.teamName),
//                ageGoe(condition.ageGoe),
//                ageLoe(condition.ageLoe)
//            )
//            .fetchCount()

        val countQuery = queryFactory
            .selectFrom(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe)
            )

        return PageableExecutionUtils.getPage(content, pageable){countQuery.fetchCount()}
        //return PageImpl(content, pageable, total)
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