package study.kotlin.querydsl.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import org.springframework.util.StringUtils.hasText
import study.kotlin.querydsl.dto.MemberSearchCondition
import study.kotlin.querydsl.dto.MemberTeamDto
import study.kotlin.querydsl.dto.QMemberTeamDto
import study.kotlin.querydsl.entity.Member
import study.kotlin.querydsl.entity.QMember.member
import study.kotlin.querydsl.entity.QTeam.team
import java.util.*
import javax.persistence.EntityManager

@Repository
class MemberJpaRepo(
    private val em : EntityManager,
    private val queryFactory : JPAQueryFactory
) {
    fun save(member : Member) : Unit = em.persist(member)

    fun findById(id : Long) : Optional<Member> = Optional.ofNullable(em.find(Member::class.java, id))

    fun findAll() : List<Member> {
        return em.createQuery("select m from Member m ",Member::class.java).resultList
    }

    fun findAllQueryDsl() : List<Member> = queryFactory.selectFrom(member).fetch()

    fun findByUsername(username: String) : List<Member> {
        return em.createQuery(
            "select m from Member m where m.username = :username",
            Member::class.java
        )
            .setParameter("username", username)
            .resultList
    }

    fun findByUsernameQueryDsl(username: String) :List<Member>{
        return queryFactory
            .selectFrom(member)
            .where(member.username.eq(username))
            .fetch()
    }

    fun searchByBuilder(condition: MemberSearchCondition) : List<MemberTeamDto> {
        val builder = BooleanBuilder()
        if(hasText(condition.username)){
            builder.and(member.username.eq(condition.username))
        }
        if(hasText(condition.teamName)){
            builder.and(team.name.eq(condition.teamName))
        }
        if(condition.ageGoe != null){
            builder.and(member.age.goe(condition.ageGoe))
        }
        if(condition.ageLoe != null){
            builder.and(member.age.loe(condition.ageLoe))
        }

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
            .leftJoin(member.team,team)
            .where(builder)
            .fetch()
    }

    fun search(condition: MemberSearchCondition) : List<MemberTeamDto> {
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
            .leftJoin(member.team,team)
            .where(
                usernameEq(condition.username),
                teamNameEq(condition.teamName),
                ageGoe(condition.ageGoe),
                ageLoe(condition.ageLoe)
            )
            .fetch()
    }

    private fun ageBetween(ageGoeCond: Int?, ageLoeCond: Int?) : BooleanExpression? {
        return ageGoe(ageGoeCond)?.and(ageLoe(ageLoeCond))
    }

    private fun ageLoe(ageLoeCond: Int?): BooleanExpression? {
        return if(ageLoeCond != null) {
            member.age.loe(ageLoeCond)
        } else{
            null
        }
    }

    private fun ageGoe(ageGoeCond: Int?): BooleanExpression? {
        return if(ageGoeCond != null) {
            member.age.goe(ageGoeCond)
        } else{
            null
        }
    }

    private fun teamNameEq(teamNameCond: String?): BooleanExpression? {
        return if(teamNameCond!=null){
            team.name.eq(teamNameCond)
        }else{
            null
        }
    }

    private fun usernameEq(nameCond: String?): BooleanExpression? {
        return if(nameCond!=null){
            member.username.eq(nameCond)
        }else{
            null
        }
    }
}