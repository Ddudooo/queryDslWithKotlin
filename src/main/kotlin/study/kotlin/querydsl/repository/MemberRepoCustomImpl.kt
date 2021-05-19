package study.kotlin.querydsl.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import study.kotlin.querydsl.dto.MemberSearchCondition
import study.kotlin.querydsl.dto.MemberTeamDto
import study.kotlin.querydsl.dto.QMemberTeamDto
import study.kotlin.querydsl.entity.QMember
import study.kotlin.querydsl.entity.QTeam

class MemberRepoCustomImpl(
    private val queryFactory: JPAQueryFactory
) : MemberRepoCustom {
    override fun search(condition: MemberSearchCondition): List<MemberTeamDto> {
        return queryFactory
            .select(
                QMemberTeamDto(
                    QMember.member.id.`as`("memberId"),
                    QMember.member.username,
                    QMember.member.age,
                    QTeam.team.id.`as`("teamId"),
                    QTeam.team.name
                )
            )
            .from(QMember.member)
            .leftJoin(QMember.member.team, QTeam.team)
            .where(
                usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe)
            )
            .fetch()
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