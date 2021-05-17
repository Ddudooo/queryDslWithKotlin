package study.kotlin.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import study.kotlin.querydsl.entity.Member
import study.kotlin.querydsl.entity.QMember.member
import study.kotlin.querydsl.entity.Team
import javax.persistence.EntityManager

@SpringBootTest
@Transactional
class QueryDslBasicTest(
    @Autowired
    private val em : EntityManager
) {
    val queryFactory = JPAQueryFactory(em)

    @BeforeEach
    fun before() {
        val teamA = Team("teamA")
        val teamB = Team("teamB")
        em.persist(teamA)
        em.persist(teamB)

        val member1 = Member("member1", 10, teamA)
        val member2 = Member("member2", 20, teamA)

        val member3 = Member("member3", 30, teamB)
        val member4 = Member("member4", 40, teamB)
        em.persist(member1)
        em.persist(member2)
        em.persist(member3)
        em.persist(member4)
    }

    @Test
    fun startJPQL(){
        //member1찾기
        val findMember1 = em.createQuery(
            "select m from Member m where m.username = :username",
            Member::class.java
        )
            .setParameter("username", "member1")
            .singleResult

        assertThat(findMember1.username).isEqualTo("member1")
    }

    @Test
    fun startQueryDsl(){
        val fetchOne = queryFactory.select(member)
            .from(member)
            .where(member.username.eq("member1"))
            .fetchOne()

        assertThat(fetchOne?.username).isEqualTo("member1")
    }

    @Test
    fun search() {
        val fetchOne = queryFactory.selectFrom(member)
            .where(
                member.username.eq("member1"),
                member.age.eq(10)
            )
            .fetchOne()

        assertThat(fetchOne?.username).isEqualTo("member1")
        assertThat(fetchOne?.age).isEqualTo(10)
    }
}